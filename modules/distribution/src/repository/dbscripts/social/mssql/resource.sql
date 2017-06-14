IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[SOCIAL_COMMENTS]') AND TYPE IN (N'U'))
CREATE TABLE SOCIAL_COMMENTS (
             id BIGINT IDENTITY(1,1) NOT NULL,
             body TEXT,
             payload_context_id VARCHAR(250),
             user_id VARCHAR(100),
             tenant_domain VARCHAR(100),
             likes SMALLINT,
             unlikes SMALLINT,
             timestamp VARCHAR(100),
             PRIMARY KEY (id)
);

IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[SOCIAL_RATING]') AND TYPE IN (N'U'))
CREATE TABLE SOCIAL_RATING (
             id BIGINT IDENTITY(1,1) NOT NULL,
             comment_id BIGINT NOT NULL,
             payload_context_id VARCHAR(250),
             user_id VARCHAR(100),
             tenant_domain VARCHAR(100),
             rating SMALLINT,
             timestamp VARCHAR(100),
             PRIMARY KEY (id),
             FOREIGN KEY (comment_id) REFERENCES SOCIAL_COMMENTS(id) ON DELETE CASCADE
);

IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[SOCIAL_RATING_CACHE]') AND TYPE IN (N'U'))
CREATE TABLE SOCIAL_RATING_CACHE (
             payload_context_id VARCHAR(250) NOT NULL,
             rating_total INT,
             rating_count INT,
             rating_average FLOAT,
             tenant_domain VARCHAR(100),
             PRIMARY KEY (payload_context_id)
);

IF NOT EXISTS (SELECT * FROM SYS.OBJECTS WHERE OBJECT_ID = OBJECT_ID(N'[DBO].[SOCIAL_LIKES]') AND TYPE IN (N'U'))
CREATE TABLE SOCIAL_LIKES (
             id BIGINT IDENTITY(1,1) NOT NULL,
             payload_context_id BIGINT NOT NULL,
             user_id VARCHAR(100),
             tenant_domain VARCHAR(100),
             like_value SMALLINT,
             timestamp VARCHAR(100),
             PRIMARY KEY (id),
             FOREIGN KEY (payload_context_id) REFERENCES SOCIAL_COMMENTS(id) ON DELETE CASCADE
);