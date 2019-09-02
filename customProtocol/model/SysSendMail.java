package customProtocol.model;

import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Marshal.OctetsStream;
import com.goldhuman.Common.Octets;
import com.goldhuman.IO.Protocol.Manager;
import com.goldhuman.IO.Protocol.ProtocolException;
import com.goldhuman.IO.Protocol.Session;

import java.io.UnsupportedEncodingException;
import protocol.GRoleInventory;

public class SysSendMail extends ProtocolExt {

    public int tid = 1;
    public int sysid = 32;
    public byte sys_type = (byte) 3;
    public int receiver;
    public Octets title;
    public Octets context;
    public GRoleInventory attach_obj;
    public int attach_money;

    public SysSendMail() {
        opcode = Opcodes.SysSendMail;
        serverType = ServerType.GDELIVERYD;
        this.title = new Octets();
        this.context = new Octets();
        this.attach_obj = new GRoleInventory();
    }

    @Override
    public OctetsStream marshal(OctetsStream os) {

        os.marshal(this.tid);
        os.marshal(this.sysid);
        os.marshal(this.sys_type);
        os.marshal(this.receiver);
        os.marshal(this.title);
        os.marshal(this.context);
        os.marshal(this.attach_obj);
        os.marshal(this.attach_money);
        return os;
    }

    @Override
    public OctetsStream unmarshal(OctetsStream stream) throws MarshalException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setTitle(String newmsg) throws UnsupportedEncodingException {
        this.title.replace(newmsg.getBytes("UTF-16LE"));
    }

    public void setContext(String newmsg) throws UnsupportedEncodingException {
        this.context.replace(newmsg.getBytes("UTF-16LE"));
    }

    @Override
    public void Process(Manager mngr, Session sn) throws ProtocolException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
