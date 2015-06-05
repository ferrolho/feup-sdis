# SDIS - CloudCanvas #
## Segundo projeto - Relatório ##

##Índice##
# Table of Contents
1. [Introdução](#intro)
2. [Arquitetura](#arquitetura)
  1. [Servidor HTTP](#serv)
  2. [TCP p2p](#tcpp2p)
  3. [Comunicação entre *peers*](#compeers)
3. [Implementação](#imp)
4. [Informações relevantes](#info)
2. [Conclusão](#conc)

## intro
##Introdução##

A aplicação desenvolvida procura oferecer aos seus utilizadores uma aplicação de desenho colaborativo para multi-plataformas 
(PC/MAC e Android). Fez-se uso de prótocolos TCP *peer-to-peer* e contém um servidor REST que trata de ligar os utilizadores a um
peer da sala a que se desejam juntar. É possível para os utilizadores criarem salas de desenho e juntarem-se a outras. Quando 
alguém se junta (ou cria) a uma sala, é apresentado o ecrã dessa mesma sala, que consiste numa tela. Cada sala é única 
(o que é desenhado na tela de uma sala não é desenhado noutras) e permite que os utilizadores desenhem da forma que quiserem 
ao mesmo tempo. 

*IMAGEM ui*

Este relatório abrangirá a arquitetura da nossa aplicação, bem como as nossas decisões de implementação, informações que achamos
relevantes transmitir e conclusão.

## arquitetura
##Arquitetura##
## serv
####Servidor HTTP####
*IMAGEM ppt que o ferrlho usou para se lembra do que estava a acontecer*

O servidor recebe e responde correctamente aos seguintes HTTP requests:

- *ip/canvas/getRoomList* - Pedido GET que devolve a lista de salas existentes assim como o Ip de alguém que se encontra de momento na sala;
- *ip/canvas/joinRoom* - Pedido POST com a query : “roomName=<nome>” , que incrementa o número de utilizadores presentes na sala;
- *ip/canvas/leaveRoom* - Pedido POST com a query “roomName=<nome>”, que decrementa o número de utilizadores presente na sala, e no caso de ser 0 a elimina;
- *ip/canvas/CreateRoom*  - Pedido POST com a query “userIp=<ip>”, que cria uma sala com o nome default “Sala” com população de 1 e com o ip do utilizador que a criou para que outros de possam juntar;

O servidor responde adequadamente com os códigos de resposta HTTP: 
- 200 em caso de sucesso;
- 400 em caso de erro de syntax ou de query;
- 404 quando acedida qualquer file ip/canvas/… que não seja valido

O servidor corre numa VPS criada com host pela DigitalOcean, pelo que o seu ip nos HTTPRequest enviados pela aplicação está afixado para esse ip/porta/protocolo;
## tcpp2p
####TCP *Peer-to-peer*####
Optamos por implementar a comunicação na nossa aplicação de desenho entre os utilizadores com uma rede *peer-to-peer* com 
ligações TCP em que os *peers* se encontram todos ligados uns aos outros.

Cada *peer* contém um server socket por onde aceita conexões novas e um socket por cada *peer* a que se encontra conectado.

## compeers
####Comunicação entre *peers*####
A comunicação entre *peers* é feita pelo envio de objectos que contêm um dos 6 tipos:
- **JOIN:** A mensagem JOIN é enviada quando um *peer* se junta à sala de forma a avisar todos os *peers* que ele se juntou;
- **GET_PEERS:** Esta mensagem é enviada quando um *peer* após se juntar a uma sala, como so tem conhecimento de um outro *peer* na sala, pedir a lista de quem se encontra na sala actualmente;
- **PEERS:** Esta mensagem é a mensagem de resposta a uma mensagem GET_PEERS e consiste num array que contem todos os IPs dos *peers* da lista do *peer* que a envia;
- **PULL_DRAWING:** Esta mensagem serve para pedir o desenho total atual da sala de forma a poder começar a desenhar na sala quando se junta ou reconecta;
- **DRAWING:** Mensagem de resposta à mensagem PULL_DRAWING que contém o desenho atual da sala;
- **CURVE:** Mensagem enviada sempre que um *peer* desenha uma linha nova, de forma a manter a sala toda com o mesmo desenho ao longo do tempo;

O protocolo da conexão entre *peers* corresponde ao envio de uma mensagem JOIN para o único peer que o *peer* conhece ao 
entrar na sala seguido imediatamente de um GET_PEERS após o qual aguarda a resposta. Depois de o *peer* atualizar a lista de 
*peers*, envia uma mensagem JOIN a cada um, seguido finalmente de um PULL_DRAWING a partir do qual começa a enviar mensagens 
CURVE sempre que desenha da sala.

Os sockets que establecem a ligação entre *peers* são criados quando o server socket de um *peer* recebe uma conexão nova com 
uma mensagem JOIN, e a partir daí a comunicação entre esses 2 *peers* é feita exclusivamente por essa conexão
Os *peers* recebem notificações de que os outros *peers* se retiram pela quebra no socket que os liga.

## imp
##Implementação##

## info
##Informações relevantes##

## conc
##Conclusão##


