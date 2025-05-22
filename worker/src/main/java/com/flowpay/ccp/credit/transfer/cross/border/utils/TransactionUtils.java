package com.flowpay.ccp.credit.transfer.cross.border.utils;

import java.util.function.Function;
import java.util.function.Predicate;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.SqlConnection;

/**
 * Utility per le transazioni
 */
public class TransactionUtils {

    private static record WrappedResult<T>(T result, Throwable error) {
        public static <T> WrappedResult<T> ok(T result) {
            return new WrappedResult<T>(result, null);
        }

        public static <T> WrappedResult<T> error(Throwable error) {
            return new WrappedResult<T>(null, error);
        }

        public Uni<T> toUni() {
            return error == null ? Uni.createFrom().item(result) : Uni.createFrom().failure(error);
        }
    }

    @FunctionalInterface
    public interface WithTransactionLike {
        <T> Uni<T> withTransaction(Function<SqlConnection, Uni<T>> function);
    }

    public static WithTransactionLike doNotRollBackOn(
            final WithTransactionLike function,
            final Class<? extends Throwable> doNotRollbackOn) {
        return new WithTransactionLike() {
            @Override
            public <T> Uni<T> withTransaction(Function<SqlConnection, Uni<T>> handler) {
                return function.withTransaction(connection -> handler.apply(connection)
                        .map(WrappedResult::ok)
                        .onFailure(doNotRollbackOn).recoverWithItem(WrappedResult::error))
                        .flatMap(WrappedResult::toUni);
            }
        };
    }

    /**
     * Wraps a `withTransaction` function and does not rollback on the given predicate,
     * but still propagates the interruption
     */
    public static WithTransactionLike doNotRollBackOn(
            final WithTransactionLike function,
            final Predicate<? super Throwable> doNotRollbackOn) {
        return new WithTransactionLike() {
            @Override
            public <T> Uni<T> withTransaction(Function<SqlConnection, Uni<T>> handler) {
                return function.withTransaction(connection -> handler.apply(connection)
                        .map(WrappedResult::ok)
                        .onFailure(doNotRollbackOn).recoverWithItem(WrappedResult::error))
                        .flatMap(WrappedResult::toUni);
            }
        };
    }
}
