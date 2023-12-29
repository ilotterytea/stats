ARG RUST_TAG=1.74
ARG BIN_NAME

FROM rust:$RUST_TAG AS builder
  ARG BIN_NAME

  WORKDIR /tmp/ilotterytea/stats/$BIN_NAME
  COPY . .

  RUN cargo build --release --package $BIN_NAME

  EXPOSE 8080

