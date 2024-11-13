CREATE TABLE documents (
                           id SERIAL PRIMARY KEY,            -- Auto-incrementing primary key
                           title VARCHAR(255) NOT NULL,      -- Title column, cannot be NULL
                           author VARCHAR(255),              -- Author column, can be NULL
                           created TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Created column with default value
);
