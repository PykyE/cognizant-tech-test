CREATE SCHEMA IF NOT EXISTS public;

CREATE TABLE public.ticket (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NULL,
    status VARCHAR(20) CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

INSERT INTO public.ticket
    (title, description, status)
    VALUES ('test ticket title #1', 'test ticket desc #1', 'OPEN');