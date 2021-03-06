package cn.flaty.NettyPush.server.codec.push;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.flaty.NettyPush.server.frame.FrameHead;
import cn.flaty.NettyPush.server.frame.SimplePushFrame;
import cn.flaty.NettyPush.utils.AssertUtils;
import cn.flaty.NettyPush.utils.MyCharsetUtil;

public class PushFrameDecoder extends ByteToMessageDecoder {
	
	private Logger log  = LoggerFactory.getLogger(PushFrameDecoder.class);

	private FrameHead frameHead;
	
	private SimplePushFrame pushFrame;

	public PushFrameDecoder(FrameHead frameHead) {
		super();
		AssertUtils.notNull(frameHead, "----> frameHead 不能为空 ");
		this.frameHead = frameHead;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
		log.info(in.refCnt()+"");
		
		int bytesHaveRead = in.readableBytes();
		if( bytesHaveRead <= frameHead.byteLength()){
			log.warn("----> 空报文！");
			in.release();
			return ;
		}
		byte [] frame = new byte[bytesHaveRead];
		in.readBytes(frame);
		pushFrame = new SimplePushFrame(frameHead, frame);
		
		// 解析报文
		String _s = this.praseFrame();
		out.add(_s);
	}

	private String praseFrame() {
		//byte encypeType = pushFrame.getEncypeType();
		byte charsetType = pushFrame.getEncypeType();
		byte [] result = pushFrame.getBody();
		//result = this.decryptBody(encypeType,pushFrame.getBody());
		
		
		return  this.getCharsetType(charsetType,result);
	}


	private String getCharsetType(byte charsetType, byte[] result) {
		byte[] _b = result;
		String s = null;
		switch (charsetType) {
		case 0:
			s = new String(_b);
			break;
		case 1:
			s = new String(_b , MyCharsetUtil.UTF_8);
			break;
		case 2:
			s = new String(_b , MyCharsetUtil.US_ASCII);
			break;
		case 3:
			s = new String(_b , MyCharsetUtil.GBK);
			break;
		case 4:
			s = new String(_b , MyCharsetUtil.GB2312);
			break;
		}
		return s;
	}

	/**
	 * 解密
	 * 
	 * @param b
	 * @return
	 */
	private byte[] decryptBody(byte encypeType,byte b[]) {
		byte[] _b = null;
		switch (encypeType) {
		case 0:
			_b = b;
			break;
		}
		return _b;

	}



}
