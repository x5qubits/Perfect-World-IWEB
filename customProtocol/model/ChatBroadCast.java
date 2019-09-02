package customProtocol.model;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;

import java.io.UnsupportedEncodingException;

public class ChatBroadCast extends ProtocolExt {

    public byte channel = (byte) 9;
    public byte emotion = (byte) 0;
    public int srcroleid = 0;
    public Octets msg;
    public Octets data;

    public ChatBroadCast(String global) throws UnsupportedEncodingException {
        this.msg = new Octets();
        this.data = new Octets();
        opcode = Opcodes.ChatBroadCast;
        serverType = ServerType.GAMEPROVIDE;
        setGlobalMessage(global);
    }

    public final void setGlobalMessage(String newmsg) throws UnsupportedEncodingException {
        this.msg.replace(newmsg.getBytes("UTF-16LE"));
    }

    @Override
    public OctetsStream marshal(OctetsStream os) {
        os.marshal(this.channel);
        os.marshal(this.emotion);
        os.marshal(this.srcroleid);
        os.marshal(this.msg);
        os.marshal(this.data);
        return os;
    }

    @Override
    public OctetsStream unmarshal(OctetsStream os) throws MarshalException {
        this.channel = os.unmarshal_byte();
        this.emotion = os.unmarshal_byte();
        this.srcroleid = os.unmarshal_int();
        os.unmarshal(this.msg);
        os.unmarshal(this.data);
        return os;
    }

    @Override
    public Object clone() {
        try {
            ChatBroadCast o = (ChatBroadCast) super.clone();
            o.msg = ((Octets) this.msg.clone());
            o.data = ((Octets) this.data.clone());
            return o;
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void Process(Manager manager, Session session) throws ProtocolException {
    }
}
