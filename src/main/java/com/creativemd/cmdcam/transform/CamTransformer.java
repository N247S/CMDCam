package com.creativemd.cmdcam.transform;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.creativemd.cmdcam.CMDCam;
import com.creativemd.cmdcam.CamEventHandler;
import com.creativemd.creativecore.transformer.CreativeTransformer;
import com.creativemd.creativecore.transformer.Transformer;
import com.creativemd.creativecore.transformer.TransformerNames;

import net.minecraft.launchwrapper.IClassTransformer;

public class CamTransformer extends CreativeTransformer implements IClassTransformer {
	
	public CamTransformer() {
		super(CMDCam.modid);
	}

	@Override
	protected void initTransformers() {
		addTransformer(new Transformer("net.minecraft.client.renderer.EntityRenderer") {
			
			@Override
			public void transform(ClassNode node) {
				MethodNode m = findMethod(node, "getMouseOver", "(F)V");
				m.instructions.insert(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/cmdcam/transform/CamMouseOverHandler", "setupMouseHandlerBefore", "()V", false));
				
				AbstractInsnNode currentNode = null;
				
				@SuppressWarnings("unchecked")
				Iterator<AbstractInsnNode> iter = m.instructions.iterator();
				
				while (iter.hasNext())
				{
					currentNode = iter.next();
					if (currentNode instanceof InsnNode && ((InsnNode)currentNode).getOpcode() == RETURN)
					{
						m.instructions.insertBefore(currentNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/cmdcam/transform/CamMouseOverHandler", "setupMouseHandlerAfter", "()V", false));
					}
				}
				
			}
		});
		
		addTransformer(new Transformer("net.minecraft.client.entity.EntityPlayerSP") {
			
			@Override
			public void transform(ClassNode node) {
				MethodNode m = findMethod(node, "isCurrentViewEntity", "()Z");
				m.instructions.clear();
				
				m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/creativemd/cmdcam/CamEventHandler", "shouldPlayerTakeInput", "()Z", false));
				m.instructions.add(new InsnNode(Opcodes.IRETURN));
				
			}
		});
		
	}
}
