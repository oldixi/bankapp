FROM alpine:latest

RUN apk add --no-cache curl bash

COPY load-docker-consul-config.sh /load-config.sh
RUN chmod +x /load-config.sh

CMD ["/load-config.sh"]