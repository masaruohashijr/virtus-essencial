FROM golang:1.12-alpine

ENV PATH="$GOPATH/bin:$GOROOT/bin:${PATH}"

#Get git
RUN apk add git

#Get unixODBC for SQL SERVER
RUN apk add gcc libc-dev g++ libffi-dev libxml2 unixodbc-dev

# Copy everything from the current directory to the PWD (Present Working Directory) inside the container
WORKDIR $GOPATH/app/virtus-essencial/
COPY . .

RUN go install

# This container exposes port 8080 to the outside world
EXPOSE 8080

# Run the binary program produced by `go install`
CMD ["virtus-essencial"]