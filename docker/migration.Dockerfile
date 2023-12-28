ARG RUST_TAG=1.74

FROM rust:$RUST_TAG AS builder
  WORKDIR /tmp/ilotterytea/stats/migrations
  COPY ./common/migrations .

  RUN cargo install diesel_cli --no-default-features --features postgres
