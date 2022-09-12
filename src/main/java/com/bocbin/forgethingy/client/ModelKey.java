package com.bocbin.forgethingy.client;

import net.minecraft.client.resources.model.ModelState;

import javax.annotation.Nullable;

/**
 * A key used to identify a set of baked quads for the baked model
 * (Records are a neat feature of (iirc) 14)
 */
public record ModelKey(
		boolean generating,
		boolean collecting,
		boolean working,
		@Nullable ModelState modelState
		) {
}
