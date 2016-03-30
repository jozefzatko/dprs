FROM nginx:latest

ENTRYPOINT ["/bin/start.sh"]
EXPOSE 80
VOLUME /templates
ENV CONSUL_URL 192.168.99.100:8500

ADD start.sh /bin/start.sh
RUN chmod +x /bin/start.sh

RUN rm -v /etc/nginx/conf.d/*.conf

ADD https://github.com/hashicorp/consul-template/releases/download/v0.7.0/consul-template_0.7.0_linux_amd64.tar.gz /usr/bin/
RUN tar -C /usr/local/bin --strip-components 1 -zxf /usr/bin/consul-template_0.7.0_linux_amd64.tar.gz