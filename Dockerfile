FROM openjdk:17

WORKDIR /arep/bin

ENV PORT=8080

COPY /target/classes /arep/bin/classes

CMD ["java","-cp","./classes","edu.escuelaing.arep.taller4.App"]