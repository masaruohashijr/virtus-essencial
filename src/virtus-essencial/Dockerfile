FROM golang:1.12-alpine

#Get unixODBC for SQL SERVER
wget ftp://ftp.unixodbc.org/pub/unixODBC/unixODBC-2.3.7.tar.gz
tar xvzf unixODBC-2.3.7.tar.gz
cd unixODBC-2.3.7/
./configure --prefix=/usr/local/unixODBC
make
make install

# Set the Current Working Directory inside the container
RUN mkdir /app/virtus
WORKDIR /app/virtus

# Copy everything from the current directory to the PWD (Present Working Directory) inside the container
COPY * .

RUN go mod download

# Build the Go app
RUN go build -o ./out/virtus .


# This container exposes port 8080 to the outside world
EXPOSE 8080

# Run the binary program produced by `go install`
CMD ["./out/virtus"]