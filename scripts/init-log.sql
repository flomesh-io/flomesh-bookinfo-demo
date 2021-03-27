CREATE TABLE default.log
(
    `uuid` String DEFAULT JSONExtractString(message, 'uuid'),
    `type` String DEFAULT JSONExtractString(message, 'type'),
    `message` String,
    `timestamp` DateTime DEFAULT now()
)
ENGINE = MergeTree
ORDER BY uuid
SETTINGS index_granularity = 8192;