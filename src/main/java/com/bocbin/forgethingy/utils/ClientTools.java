package com.bocbin.forgethingy.utils;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

public class ClientTools {

	// created by MCJty to make the model generation code cleaner (not that it's particularly clean)
	// but this code also looks like death so
	// yeah

	private static void putVertex(BakedQuadBuilder builder, Vector3f normal, Vector4f vector,
								  float u, float v, TextureAtlasSprite sprite) {
		var elements = builder.getVertexFormat().getElements();
		for (int j = 0; j < elements.size(); j++) {
			VertexFormatElement e = elements.get(j);
			switch (e.getUsage()) {
				case POSITION -> builder.put(j, vector.x(), vector.y(), vector.z(), 1f);
				case COLOR -> builder.put(j, 1f, 1f, 1f, 1f);
				case UV -> putVertexUV(builder, u, v, sprite, j, e);
				case NORMAL -> builder.put(j, normal.x(), normal.y(), normal.z());
				default -> builder.put(j);
			}
		}
	}

	private static void putVertexUV(BakedQuadBuilder builder, float u, float v, TextureAtlasSprite sprite, int j, VertexFormatElement e) {
		// does the correct uv mapping
		switch (e.getIndex()) {
			case 0 -> builder.put(j, sprite.getU(u), sprite.getV(v));
			case 2 -> builder.put(j, (short) 0, (short) 0);  // not sure why is 0
			default -> builder.put(j);
		}
	}

	// quad creation method
	public static BakedQuad createQuad(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4,
									   Transformation rotation, TextureAtlasSprite sprite) {
		// black magic to find a normal vector
		Vector3f normal = v3.copy();
		normal.sub(v2);
		Vector3f temp = v1.copy();
		temp.sub(v2);
		normal.cross(temp);
		normal.normalize();

		int spriteWidth = sprite.getWidth();
		int spriteHeight = sprite.getHeight();

		rotation = rotation.blockCenterToCorner();
		rotation.transformNormal(normal);

		Vector4f vec1 = new Vector4f(v1); rotation.transformPosition(vec1);
		Vector4f vec2 = new Vector4f(v2); rotation.transformPosition(vec2);
		Vector4f vec3 = new Vector4f(v3); rotation.transformPosition(vec3);
		Vector4f vec4 = new Vector4f(v4); rotation.transformPosition(vec4);

		var builder = new BakedQuadBuilder(sprite);
		builder.setQuadOrientation(Direction.getNearest(normal.x(), normal.y(), normal.z()));
		putVertex(builder, normal, vec1, 0, 0, sprite);
		putVertex(builder, normal, vec2, 0, spriteHeight, sprite);
		putVertex(builder, normal, vec3, spriteWidth, spriteHeight, sprite);
		putVertex(builder, normal, vec4, spriteWidth, 0, sprite);
		return builder.build();
	}

	public static Vector3f v3(float x, float y, float z) {
		return new Vector3f(x,y,z);
	}
}
