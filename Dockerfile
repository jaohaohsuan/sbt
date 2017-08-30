FROM fabric8/java-jboss-openjdk8-jdk:1.2

ENV HOME=/home/jenkins

USER root

RUN mkdir -p $HOME && \
  sed -i "s|\/root|$HOME|g" /etc/passwd && \
  yum install -y bash curl tree git vim && \
  curl -Lsk https://cocl.us/sbt01316tgz | tar -zxC /var

ENV PATH=/var/sbt/bin:$PATH

RUN mkdir ~/proj1 && cd $_ \
  && sbt about \
  && rm -rf ~/proj1

ADD plugins.sbt $HOME/.sbt/0.13/plugins/build.sbt

RUN mkdir ~/proj1 && cd $_ \
  && sbt about \
  && rm -rf ~/proj1

VOLUME ["$HOME/.sbt","$HOME/.ivy2"]
WORKDIR $HOME