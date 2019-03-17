FROM ascdc/jdk8
RUN mkdir -p /data /workspace
VOLUME ["/data","/workspace"]
WORKDIR /workspace
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dport=8888","app.jar"]

EXPOSE 8888