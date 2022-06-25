--liquibase formatted sql

--changeset frmi:2

ALTER TABLE dlq_record ADD COLUMN dequeued_at timestamp without time zone;

-- ROLLBACK ALTER TABLE dlq_record DROP COLUMN dequeued_at;