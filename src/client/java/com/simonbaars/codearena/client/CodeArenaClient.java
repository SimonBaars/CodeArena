package com.simonbaars.codearena.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client entry. Legacy Swing {@code CodeEditor} / RSyntaxTextArea desktop windows are
 * intentionally not opened — Minecraft 26.x + modern JDKs do not integrate well with
 * that Forge-era approach. See MIGRATION.md.
 */
public class CodeArenaClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("codearena");

	@Override
	public void onInitializeClient() {
		LOGGER.info("CodeArena client init — Swing code editor stubbed/dropped");
	}
}
