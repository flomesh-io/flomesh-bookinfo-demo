CREATE TABLE default.log
(
    `startTime` Int64 DEFAULT JSONExtractInt(message, 'startTime'),
    `endTime` Int64 DEFAULT JSONExtractInt(message, 'endTime'),
    `latency` Int64 DEFAULT JSONExtractInt(message, 'latency'),
    `status` Int16 DEFAULT JSONExtractInt(response, 'status'),
    `statusText` String DEFAULT JSONExtractString(response, 'statusText'),
    `protocol` String DEFAULT JSONExtractString(message, 'protocol'),
    `method` String DEFAULT JSONExtractString(message, 'method'),
    `path` String DEFAULT JSONExtractString(message, 'path'),
    `headers` String DEFAULT JSONExtractRaw(message, 'headers'),
    `body` String DEFAULT JSONExtractString(message, 'body'),
    `response` String DEFAULT JSONExtractRaw(message, 'response'),
    `response.protocol` String DEFAULT JSONExtractString(response, 'protocol'),
    `message` String
)
ENGINE = MergeTree
PARTITION BY (toYYYYMM(toDateTime(startTime / 1000)))
ORDER BY (status, startTime)
SETTINGS index_granularity = 8192;
