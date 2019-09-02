package customProtocol.model;

import com.goldhuman.Common.Conf;
import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.xml.bind.DatatypeConverter;

public class GameSocketConnect {

    public static Socket GetConnection(int type) throws IOException {
        Socket socket;
        switch (type) {
            case 1:
                socket = new Socket(Conf.GetInstance().find("DeliveryClient", "address"), 29300);//29300
                return socket;
            case 0:
                socket = new Socket(Conf.GetInstance().find("DeliveryClient", "address"), 29100);//29100
                return socket;
            default:
                return null;
        }
    }

    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    public static void Close(Socket connection) throws IOException {
        connection.close();
    }

    public static OctetsStream Encode(OctetsStream paramOctetsStream, ProtocolExt packet) {
        paramOctetsStream.compact_uint32(packet.opcode.getOpcode()).marshal(new OctetsStream().marshal(packet));
        return paramOctetsStream;
    }

    public static OctetsStream EncodeRpc(OctetsStream paramOctetsStream, RpcDataExt packet) {
        paramOctetsStream.compact_uint32(packet.opcode.getOpcode()).marshal(new OctetsStream().marshal(packet));
        return paramOctetsStream;
    }

    public static void Send(ProtocolExt packet) throws IOException {
        Socket connection = GetConnection(packet.serverType.ordinal());
        OutputStream output = connection.getOutputStream();
        OctetsStream octeto = new OctetsStream();
        output.write(Encode(octeto, packet).getBytes());
        Close(connection);
    }

    public static OctetsStream SendRpcData(RpcDataExt packet) throws IOException, MarshalException {
        byte[] buf = new byte[524288];
        int bytesRead;
        Socket connection = GetConnection(packet.serverType.ordinal());
        OutputStream output = connection.getOutputStream();
        OctetsStream octeto = new OctetsStream();
        InputStream input = connection.getInputStream();
        output.write(EncodeRpc(octeto, packet).getBytes());
        OctetsStream streamFromBuff = null;
        while ((bytesRead = input.read(buf)) != -1) {
            Octets frombuf = new Octets(buf);
            streamFromBuff = new OctetsStream(frombuf);
            int opcode = streamFromBuff.uncompact_uint32();
            if (opcode == packet.opcode.getOpcode()) {
                int length = streamFromBuff.uncompact_uint32();
                streamFromBuff.unmarshal_int();
                streamFromBuff.unmarshal_int();
                break;
            }
        }
        Close(connection);
        return streamFromBuff;
    }
}
