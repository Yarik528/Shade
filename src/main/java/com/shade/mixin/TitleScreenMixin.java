package com.shade.mixin;

import com.shade.gui.AltScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin() { super(new LiteralText("")); }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        TitleScreen self = (TitleScreen)(Object)this;
        int btnW = 100;
        int btnH = 20;
        int x = this.width / 2 - btnW / 2;
        int y = this.height / 4 + 130;

        addButton(new ButtonWidget(x, y, btnW, btnH,
            new LiteralText("Alts"),
            b -> client.openScreen(new AltScreen(self))));
    }
}
