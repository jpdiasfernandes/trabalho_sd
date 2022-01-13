package middleware;

import businesslogic.excecoes.*;
import businesslogic.GestaoLN;
import frames.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class ExecuteLogic implements Runnable{
    private  Middleware middleware;
    private GestaoLN gestaoLN;

    public ExecuteLogic(Middleware middleware, GestaoLN gestaoLN) {
        this.middleware = middleware;
        this.gestaoLN = gestaoLN;
    }

    @Override
    public void run() {
        while(true){
            Map.Entry<Integer, SerializerFrame> req = middleware.bufferConsume();
            int id = req.getKey();
            SerializerFrame frame = req.getValue();
            switch (frame.opCode){
                case(0):
                    FrameRegisto frameRegisto = null;
                    System.out.println("Detectei opcode de registo");
                    try {
                        frameRegisto = FrameRegisto.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        gestaoLN.registarUtilizador(frameRegisto.requestUsername,frameRegisto.requestPwd);
                        System.out.println("Registei");
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, 0, null);
                        System.out.println("Vou colocar na Response");
                        middleware.putResponse(r, id);
                    }catch (UsernameExistenteException e){
                        frameRegisto.initializeError(e.getLocalizedMessage());
                        System.out.println("Já existe");
                        try {
                            byte[] replyError = frameRegisto.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            middleware.putResponse(r, id);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case(1):
                    FrameLogin frameLogin = null;
                    System.out.println("Detectei opcode de login");
                    try {
                        frameLogin = FrameLogin.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        gestaoLN.validarUtilizador(frameLogin.requestUsername,frameLogin.requestPwd);
                        boolean admin = gestaoLN.admin(frameLogin.requestUsername);
                        System.out.println("É admin " + admin);
                        frameLogin.initializeReply(middleware.putToken(frameLogin.requestUsername,frameLogin.requestPwd), admin);
                        try {
                            byte[] replyReply = frameLogin.serializeReply();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, replyReply.length, replyReply);
                            middleware.putResponse(r, id);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }catch (UsernameNaoExistenteException | PasswordErradaException e){
                        frameLogin.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameLogin.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            middleware.putResponse(r, id);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case(2):
                    FrameInserirVoo frameInserirVoo = null;
                    try {
                        frameInserirVoo = FrameInserirVoo.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        gestaoLN.insercaoVoo(middleware.getUserName(frame.token),frameInserirVoo.requestOrigem,frameInserirVoo.requestDestino,frameInserirVoo.requestCapacidade);
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, 0, null);
                        middleware.putResponse(r, id);
                    }catch (NaoTemPermissaoException | UsernameNaoExistenteException e){
                        frameInserirVoo.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameInserirVoo.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            middleware.putResponse(r, id);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case(3):
                    FrameCancelarVoos frameCancelarVoos = null;
                    try {
                        frameCancelarVoos = FrameCancelarVoos.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        gestaoLN.cancelarDia(frameCancelarVoos.requestData);
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, 0, null);
                        middleware.putResponse(r, id);
                    }catch (DataSemVoosException e){
                        frameCancelarVoos.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameCancelarVoos.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            middleware.putResponse(r, id);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case(4):
                    FrameReservarViagem frameReservarViagem = null;
                    try {
                        frameReservarViagem = FrameReservarViagem.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        int codReserva = gestaoLN.reservarViagem(middleware.getUserName(frame.token),frameReservarViagem.requestDataInicial,frameReservarViagem.requestDataFinal,frameReservarViagem.requestDestinos);
                        frameReservarViagem.initializeReply(codReserva);
                        try {
                            byte[] replyReply = frameReservarViagem.serializeReply();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, replyReply.length, replyReply);
                            middleware.putResponse(r, id);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }catch (VooIndisponivelException | UsernameNaoExistenteException e){
                        frameReservarViagem.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameReservarViagem.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            middleware.putResponse(r, id);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case(5):
                    FrameCancelarReserva frameCancelarReserva = null;
                    try {
                        frameCancelarReserva = FrameCancelarReserva.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        gestaoLN.cancelarReserva(middleware.getUserName(frame.token),frameCancelarReserva.requestCodReserva);
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, 0, null);
                        middleware.putResponse(r, id);
                    }catch (UsernameNaoExistenteException | VooIndisponivelException e){
                        frameCancelarReserva.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameCancelarReserva.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            middleware.putResponse(r, id);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;
                case(6):
                    FramePedirVoos framePedirVoos = new FramePedirVoos();
                    List<Map.Entry<String, String>> voos = gestaoLN.getVoos();
                    framePedirVoos.initializeReply(voos);
                    try {
                        byte[] replyReply = framePedirVoos.serializeReply();
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, replyReply.length, replyReply);
                        middleware.putResponse(r, id);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
                case(7):

                break;
            }
        }
    }
}
