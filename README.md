Objetivo: A ideia é gerar novas assinaturas sem causar impacto para cliente e negocio.

Pensando em aplicações distribuidas segui com a estrutura abaixo de stask e eveolucoes: 


Sobre as stacks Skil vinculadas: 

JAVA 21, 
POSTGRES,
Arquitetura DDD + AGGREGATE 
Swagger

Stacks futuras : 
Redis, 
Kafka,
flyway

 == REPORT DATA 17/06/2026
Nossa configuracao interna utilizar o Hibernate apenas como Orquestrador, a criação de tabelas e estrutuas de DDL será executada via ferramente Flyway.

Hoje temos arquivo docker-compose para geração de imagem, gerando as configurações disposta no arquivo. 
Em contra partida vamos precisar executar manualmente as migration que estao no resources do src.

URL swagger - > http://localhost:8080/swagger-ui/index.html
