package org.evomaster.client.java.controller.internal.db.constraint.expr;

import java.util.Objects;

public class SqlIsNullCondition extends SqlCondition {

    private final /*non-null*/ SqlColumn columnName;

    public SqlIsNullCondition(SqlColumn columnName) {
        if (columnName == null) {
            throw new IllegalArgumentException("columnName cannot be null");
        }
        this.columnName = columnName;
    }

    @Override
    public String toSql() {
        return String.format("%s IS NULL", columnName.toSql());
    }

    @Override
    public <K, V> K accept(SqlConditionVisitor<K, V> visitor, V argument) {
        return visitor.visit(this, argument);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlIsNullCondition that = (SqlIsNullCondition) o;
        return columnName.equals(that.columnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnName);
    }
}
