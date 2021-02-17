FROM golang:1.12-alpine

RUN apk add build-base

#Get unixODBC for SQL SERVER
WORKDIR /opt
RUN wget ftp://ftp.unixodbc.org/pub/unixODBC/unixODBC-2.3.7.tar.gz
RUN tar xvzf unixODBC-2.3.7.tar.gz
WORKDIR /opt/unixODBC-2.3.7
RUN ./configure --prefix=/usr/local/unixODBC
RUN make
RUN make install

# Set the Current Working Directory inside the container
RUN mkdir -p /app/virtus-essencial

# Copy everything from the current directory to the PWD (Present Working Directory) inside the container
WORKDIR $GOPATH/app/virtus-essencial/
COPY . .

# This container exposes port 8080 to the outside world
EXPOSE 8080

# Run the binary program produced by `go install`
CMD ["./virtus"]