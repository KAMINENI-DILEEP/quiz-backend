package com.exam.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "display_id_sequences")
public class DisplayIdSequence {

    @Id
    @Column(name = "prefix", length = 3, nullable = false)
    private String prefix;

    @Column(name = "sequence_value", nullable = false)
    private Long lastValue = 0L;

    public DisplayIdSequence() {
    }

    public DisplayIdSequence(String prefix, Long lastValue) {
        this.prefix = prefix;
        this.lastValue = lastValue;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getLastValue() {
        return lastValue;
    }

    public void setLastValue(Long lastValue) {
        this.lastValue = lastValue;
    }
}
