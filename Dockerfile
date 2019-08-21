FROM maven:latest AS maven-build

MAINTAINER  Aymen Furter <aymen.furter@gmail.com>

COPY src /opt/todotxt/src
COPY pom.xml /opt/todotxt
RUN mvn -f /opt/todotxt/pom.xml clean package

FROM openjdk:latest

RUN yum -y install vim && yum -y install git

RUN mkdir /root/.vim && mkdir /root/.vim/bundle \
    && git clone https://github.com/VundleVim/Vundle.vim.git ~/.vim/bundle/Vundle.vim \
    && (cd /root && curl -O https://raw.githubusercontent.com/aymenfurter/dotfiles/master/.vimrc) \
    && mkdir /root/.vim/plugin && (cd /root/.vim/plugin && curl -O https://raw.githubusercontent.com/djoshea/vim-autoread/master/plugin/autoread.vim)

COPY --from=maven-build /opt/todotxt/target/todotxt-fat-jar-with-dependencies.jar /usr/local/lib/todotxt.jar

ENTRYPOINT java -jar /usr/local/lib/todotxt.jar & vim /root/todo/todo.txt
