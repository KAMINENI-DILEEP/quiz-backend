package com.quizportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "display_id_sequences")
public class DisplayIdSequence {

    @Id
    @Column(length = 10)
    private String prefix;

    @Column(name = "sequence_value", nullable = false)
    private Long sequenceValue;

    public DisplayIdSequence() {
    }

    public DisplayIdSequence(String prefix, Long sequenceValue) {
        this.prefix = prefix;
        this.sequenceValue = sequenceValue;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getSequenceValue() {
        return sequenceValue;
    }

    public void setSequenceValue(Long sequenceValue) {
        this.sequenceValue = sequenceValue;
    }
}