package com.flowpay.ccp.credit.transfer.cross.border.workers.accredito;

import com.flowpay.ccp.credit.transfer.cross.border.Constants;
import com.flowpay.ccp.credit.transfer.cross.border.Utils;
import com.flowpay.ccp.credit.transfer.cross.border.accrediti.mappers.AccreditoToPersistenceContext;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.ServiceConfig;
import com.flowpay.ccp.credit.transfer.cross.border.dto.FileReceived;
import com.flowpay.ccp.credit.transfer.cross.border.errors.MappingError;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.BonificoInIngresso;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.accredito.MappaturaBonificoInIngresso;
import com.flowpay.ccp.job.JobData;
import com.flowpay.ccp.job.JobPublisher;
import com.flowpay.ccp.job.JobSubscriber;
import com.flowpay.ccp.persistence.DataSources;
import com.prowidesoftware.swift.model.mx.AbstractMX;
import command.application.global.ec.netgat.gps.eu.ApplicationCommandType;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Named;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ApplicationScoped
public class AccreditoWorker {

    private static final Logger LOG = Logger.getLogger(AccreditoWorker.class);


    private final JobSubscriber externalJobSubscriber;

    private final DataSources dataSources;
    private final BanksConfig banksConfig;

    AccreditoWorker(
            @Channel(Constants.FTP_SERVICE_INCOMING_CHANNEL) Multi<Message<JobData>> externalEmitter,
            DataSources dataSources,
            BanksConfig banksConfig
    ) {
        this.externalJobSubscriber = new JobSubscriber(externalEmitter);

        this.dataSources = dataSources;
        this.banksConfig = banksConfig;
    }

    public void onStart(@Observes StartupEvent event) {
        LOG.info("Starting AccreditoWorker");

        this.externalJobSubscriber.subscribe(
                Constants.JOB_NEW_INCOMING_XML,
                new FileReceived.Deserializer(),
                this::handleNewXML
        );
    }

    private AbstractMX parseBffCommand(String file) {
        try {
            var instance = JAXBContext.newInstance(JAXBElement.class, ApplicationCommandType.class);
            var unmarshaller = instance.createUnmarshaller();
            var source = new StringReader(file);
            var result = (JAXBElement<ApplicationCommandType>) unmarshaller.unmarshal(source);
            var data = result.getValue().getData();
            if (data.getDataEncoded() != null) {
                var encoded = data.getDataEncoded();
                var buffer = encoded.getBuffer();
                var decoded = Base64.getDecoder().decode(buffer);
                var string = new String(decoded, StandardCharsets.UTF_8);
                return AbstractMX.parse("<RequestPayload>"+string+"</RequestPayload>");
            }
            var value = data.getXmlData();
            var list = value.getAny();
            if (list.size() != 2) {
                throw new MappingError("file ricevuto non contiene un messaggio swift correttamente formattato");
            }

            var header = list.get(0);
            var document = list.get(1);
            var element = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().newDocument();
            var request = element.createElement("RequestPayload");
            var headerElement = element.importNode(header, true);
            var documentElement = element.importNode(document, true);
            request.appendChild(headerElement);
            request.appendChild(documentElement);
            var sw = new StringWriter();
            var transformer = TransformerFactory.newDefaultInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(request), new StreamResult(sw));
            return AbstractMX.parse(sw.toString());
        } catch (JAXBException e) {
            throw new MappingError(e);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private AbstractMX parseXML(FileReceived fileReceived) {
        var fileContent = Base64.getDecoder().decode(fileReceived.b64File());
        var fileAsString = new String(fileContent, StandardCharsets.UTF_8);
        var bankInfo = banksConfig.bank().get(fileReceived.abi());
        var channelInfo = switch (fileReceived.tipologia()) {
            case CBPR -> bankInfo.channel().cbpr();
            case T2 -> bankInfo.channel().t2();
        };

        if (channelInfo.tramitatoDa().isPresent()) {
            return parseBffCommand(fileAsString);
        } else {
            return AbstractMX.parse("<RequestPayload>"+fileAsString+"</RequestPayload>");
        }
    }

    private Uni<Void> handleNewXML(FileReceived fileReceived) {
        LOG.info(
                "nuovo file ricevuto: abi: %s, filename: %s, tipologia %s"
                .formatted(fileReceived.abi(), fileReceived.fileName(), fileReceived.tipologia())
        );
        var connection = this.dataSources.getDataSource(fileReceived.abi());

        var xml = parseXML(fileReceived);
        var namespace = xml.getNamespace();

        var repository = new MappaturaBonificoInIngresso.Entity().repository(connection);

        return repository.getByNamespace(namespace)
        .flatMap(mappatura -> {
            var mapper = Utils.loadAccreditoMapper(mappatura.classeQualificataNomeCompleto());
            var bonifico = mapper.fromXmlToDBMapper().map(
                    xml,
                    new AccreditoToPersistenceContext(
                            fileReceived.abi(),
                            0L,
                            fileReceived.tipologia().sistemaDiRegolamento(),
                            mappatura.id()));

            var entity = new BonificoInIngresso.Entity();
            return entity.repository(connection).run(entity.insert(bonifico));
        });
    }
}
