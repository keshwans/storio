package com.pushtorefresh.storio.sqlite;

import com.pushtorefresh.storio.sqlite.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operation.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResolver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SQLiteTypeMappingTest {

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullPutResolver() {
        new SQLiteTypeMapping.Builder<Object>()
                .putResolver(null)
                .getResolver(mock(GetResolver.class))
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapFromCursor() {
        new SQLiteTypeMapping.Builder<Object>()
                .putResolver(mock(PutResolver.class))
                .getResolver(null)
                .deleteResolver(mock(DeleteResolver.class))
                .build();
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Test(expected = NullPointerException.class)
    public void nullMapToDeleteQuery() {
        new SQLiteTypeMapping.Builder<Object>()
                .putResolver(mock(PutResolver.class))
                .getResolver(mock(GetResolver.class))
                .deleteResolver(null)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void build() {
        class TestItem {

        }

        final PutResolver<TestItem> putResolver = mock(PutResolver.class);
        final GetResolver<TestItem> getResolver = mock(GetResolver.class);
        final DeleteResolver<TestItem> deleteResolver = mock(DeleteResolver.class);

        final SQLiteTypeMapping<TestItem> typeMapping = new SQLiteTypeMapping.Builder<TestItem>()
                .putResolver(putResolver)
                .getResolver(getResolver)
                .deleteResolver(deleteResolver)
                .build();

        assertEquals(putResolver, typeMapping.putResolver());
        assertEquals(getResolver, typeMapping.getResolver());
        assertEquals(deleteResolver, typeMapping.deleteResolver());
    }
}
