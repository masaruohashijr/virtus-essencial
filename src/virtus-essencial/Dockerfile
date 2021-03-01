#FROM golang:1.12-alpine
FROM golang:1.16-alpine

ENV PATH="$GOPATH/bin:$GOROOT/bin:${PATH}"

#Get some tools
RUN apk add git curl

#Get unixODBC for SQL SERVER
RUN apk add gcc libc-dev g++ libffi-dev libxml2 unixodbc-dev

#Install Microsoft ODBC 17
#Download the desired package(s)
RUN curl -O https://download.microsoft.com/download/e/4/e/e4e67866-dffd-428c-aac7-8d28ddafb39b/msodbcsql17_17.7.1.1-1_amd64.apk
#Install the package(s)
RUN apk add --allow-untrusted msodbcsql17_17.7.1.1-1_amd64.apk


# Copy everything from the current directory to the PWD (Present Working Directory) inside the container
WORKDIR $GOPATH/app/virtus-essencial/
COPY . .

#Create odbc.ini
RUN odbcinst -i -s -f config/template_odbc_ini_file

RUN go install

# This container exposes port 8080 to the outside world
EXPOSE 8080

# Run the binary program produced by `go install`
CMD ["virtus-essencial"]