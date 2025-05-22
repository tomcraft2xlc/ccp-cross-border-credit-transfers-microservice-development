package com.flowpay.ccp.credit.transfer.cross.border;

import com.flowpay.ccp.credit.transfer.cross.border.accrediti.MapperAccredito;
import com.flowpay.ccp.credit.transfer.cross.border.configuration.BanksConfig;
import com.flowpay.ccp.credit.transfer.cross.border.mappers.ISOMapper;
import com.flowpay.ccp.credit.transfer.cross.border.persistence.credit.transfer.SistemaDiRegolamento;
import io.smallrye.mutiny.CompositeException;
import org.jboss.logging.Logger;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.List;

public class Utils {

    private final static Logger logger = Logger.getLogger(Utils.class);

    private Utils() {
    }

    public static <T> T allFieldsEmpty(T object) {
        if (object == null) {
            return null;
        }
        logger.debug("checking: " + object.getClass());
        try {
            for (var property : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
                if (property.getName().equals("class")) {
                    logger.debug("property: " + property.getName() + ", ignoring property");
                    continue;
                }
                var method = property.getReadMethod();
                logger.debug("property: " + property.getName());
                if (method != null) {
                    var value = method.invoke(object);
                    if (value instanceof List<?> list) {
                        if (!list.isEmpty()) {
                            logger.debug("is a list with some elements in it, returning the object, SUCCESS");
                            return object;
                        }
                    } else {
                        if (value != null) {
                            logger.debug("is not null, returning the object, SUCCESS, value: " + value);
                            return object;
                        }
                    }

                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        logger.debug("no property was found with values, FAILURE");
        return null;
    }

    public static ISOMapper loadClass(String className) throws ClassCastException, CompositeException {
        try {
            var clazz = Class.forName(className);
            var result = clazz.getDeclaredConstructor().newInstance();
            if (result instanceof ISOMapper mapping) {
                return mapping;
            } else {
                throw new ClassCastException("Class " + className + " is not a valid mapping class");
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new CompositeException(e);
        }
    }

    public static MapperAccredito loadAccreditoMapper(String className) throws ClassCastException, CompositeException {
        try {
            var clazz = Class.forName(className);
            var result = clazz.getDeclaredConstructor().newInstance();
            if (result instanceof MapperAccredito mapping) {
                return mapping;
            } else {
                throw new ClassCastException("Class " + className + " is not a valid mapping class");
            }
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new CompositeException(e);
        }
    }

    /**
     * Random Number Generator
     * <p>
     * Usando SecureRandom perchè l'implementazione di default è molto prevedibile.
     */
    public static final SecureRandom numberGenerator = new SecureRandom();

    /**
     * Genera il TID. Per ora usa un numero casuale di 11 cifre.
     * 
     * @return Il TID generato.
     */
    public static String generateTID() {
        return String.valueOf(numberGenerator.nextLong(100000000000L));
    }

    public static BigDecimal calcolaImportoDiAddebito(BigDecimal importo, BigDecimal cambio) {
        // TODO: check this algorithm
        BigDecimal importoDiAddebito = importo.multiply(cambio);
        importoDiAddebito = importoDiAddebito.setScale(2, RoundingMode.HALF_EVEN);
        return importoDiAddebito;
    }

    /**
     * Converts a date string in Italian format (dd/MM/yyyy) to a SQL-compatible
     * date string (yyyy-MM-dd).
     *
     * <p>
     * This method parses the input string using the pattern "dd/MM/yyyy" and
     * converts it to
     * a {@link java.time.LocalDate}. The resulting {@code LocalDate} is then
     * formatted to its
     * default ISO-8601 string representation, which matches the SQL {@code DATE}
     * format (yyyy-MM-dd).
     *
     * @param italianDate the date string in Italian format (e.g., "07/04/2025")
     * @return the date string formatted as "yyyy-MM-dd" suitable for SQL usage
     * @throws java.time.format.DateTimeParseException if the input string cannot be
     *                                                 parsed
     */
    public static String italianDateToSqlDate(String italianDate) {
        DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(italianDate, italianFormatter).toString();
    }

    
    /**
     * Find the next possible business day, given a start date.
     * 
     * This is done by checking if the day and then moving forward until a possible business day is found.
     * 
     * See also {@link #isPossibleBusinessDay}
     */
    public static LocalDate nextPossibleBusinessDay(LocalDate data, BanksConfig.BankConfig config, SistemaDiRegolamento sistemaDiRegolamento) {
        var info = sistemaDiRegolamento == SistemaDiRegolamento.TARGET ? config.channel().t2() : config.channel().cbpr();
        var today = LocalDate.now();
        if (data.equals(today)) {
            var now = LocalDateTime.now();
            var cutOff = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), info.oraCutOff().orElse(17), info.minutoCutOff().orElse(0));
            if (now.isBefore(cutOff) && isPossibleBusinessDay(data, config)) {
                return data;
            }

        }
        do {
            data = data.plusDays(1);
        } while (!isPossibleBusinessDay(data, config));
        return data;
    }

    /**
     * Return if a given date is a possible business day.
     * 
     * This runs only trivial checks, like if it's a weekend or not. The date must
     * then be then confirmed by CIP to be a true business day.
     */
    public static boolean isPossibleBusinessDay(LocalDate data, BanksConfig.BankConfig config) {
        DayOfWeek dayOfWeek = data.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

}
