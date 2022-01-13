package server.middleware;

import server.businesslogic.excecoes.*;
import server.businesslogic.GestaoLN;
import server.frames.*;

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
            //SerializerFrame frame = middleware.buffer.consume();
            SerializerFrame frame = null;
            switch (frame.opCode){
                case(0):
                    FrameRegisto frameRegisto = new FrameRegisto();
                    try {
                        frameRegisto.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        gestaoLN.registarUtilizador(frameRegisto.requestUsername,frameRegisto.requestPwd);
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, 0, null);
                        //adicionar ao middlware.map
                    }catch (UsernameExistenteException e){
                        frameRegisto.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameRegisto.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            //adicionar ao middlware.map
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                case(1):
                    FrameLogin frameLogin = new FrameLogin();
                    try {
                        frameLogin.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        gestaoLN.validarUtilizador(frameLogin.requestUsername,frameLogin.requestPwd);
                        boolean admin = gestaoLN.admin(frameLogin.requestUsername);
                        frameLogin.initializeReply(middleware.putToken(frameLogin.requestUsername,frameLogin.requestPwd), admin);
                        try {
                            byte[] replyReply = frameLogin.serializeReply();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, replyReply.length, replyReply);
                            //adicionar ao middlware.map
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }catch (UsernameNaoExistenteException | PasswordErradaException e){
                        frameLogin.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameLogin.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            //adicionar ao middlware.map
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                case(2):
                    FrameInserirVoo frameInserirVoo = new FrameInserirVoo();
                    try {
                        frameInserirVoo.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        gestaoLN.insercaoVoo(middleware.getUserName(frame.token),frameInserirVoo.requestOrigem,frameInserirVoo.requestDestino,frameInserirVoo.requestCapacidade);
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, 0, null);
                        //adicionar ao middlware.map
                    }catch (NaoTemPermissaoException | UsernameNaoExistenteException e){
                        frameInserirVoo.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameInserirVoo.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            //adicionar ao middlware.map
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                case(3):
                    FrameCancelarVoos frameCancelarVoos = new FrameCancelarVoos();
                    try {
                        frameCancelarVoos.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try{
                        gestaoLN.cancelarDia(frameCancelarVoos.requestData);
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, 0, null);
                        //adicionar ao middlware.map
                    }catch (DataSemVoosException e){
                        frameCancelarVoos.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameCancelarVoos.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            //adicionar ao middlware.map
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                case(4):
                    FrameReservarViagem frameReservarViagem = new FrameReservarViagem();
                    try {
                        frameReservarViagem.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        int codReserva = gestaoLN.reservarViagem(middleware.getUserName(frame.token),frameReservarViagem.requestDataInicial,frameReservarViagem.requestDataFinal,frameReservarViagem.requestDestinos);
                        frameReservarViagem.initializeReply(codReserva);
                        try {
                            byte[] replyReply = frameReservarViagem.serializeReply();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, replyReply.length, replyReply);
                            //adicionar ao middlware.map
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }catch (VooIndisponivelException | UsernameNaoExistenteException e){
                        frameReservarViagem.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameReservarViagem.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            //adicionar ao middlware.map
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                case(5):
                    FrameCancelarReserva frameCancelarReserva = new FrameCancelarReserva();
                    try {
                        frameCancelarReserva.deserialize(frame.data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        gestaoLN.cancelarReserva(middleware.getUserName(frame.token),frameCancelarReserva.requestCodReserva);
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, 0, null);
                        //adicionar ao middlware.map
                    }catch (UsernameNaoExistenteException | VooIndisponivelException e){
                        frameCancelarReserva.initializeError(e.getLocalizedMessage());
                        try {
                            byte[] replyError = frameCancelarReserva.serializeError();
                            ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x0, replyError.length, replyError);
                            //adicionar ao middlware.map
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                case(6):
                    FramePedirVoos framePedirVoos = new FramePedirVoos();
                    List<Map.Entry<String, String>> voos = gestaoLN.getVoos();
                    framePedirVoos.initializeReply(voos);
                    try {
                        byte[] replyReply = framePedirVoos.serializeReply();
                        ReplySerializerFrame r = new ReplySerializerFrame(frame.tag, (byte) 0x1, replyReply.length, replyReply);
                        //adicionar ao middlware.map
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                case(7):

                break;
            }
        }
    }
}
