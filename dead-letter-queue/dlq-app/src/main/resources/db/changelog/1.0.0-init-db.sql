--liquibase formatted sql

--changeset frmi:1

CREATE SEQUENCE IF NOT EXISTS dlq_record_sequence
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

CREATE TABLE IF NOT EXISTS dlq_record
(
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    dequeued boolean NOT NULL,
    dequeued_at timestamp without time zone,
    exception oid,
    entry oid NOT NULL,
    CONSTRAINT dlq_record_pkey PRIMARY KEY (id)
)

-- ROLLBACK DROP SEQUENCE IF EXISTS dlq_record_sequence;
-- ROLLBACK DROP TABLE IF EXISTS dlq_record;