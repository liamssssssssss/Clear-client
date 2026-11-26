package com.clearclient;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClearClickGui extends Screen {
    private static final int BG = 0xF0111217, CARD = 0xFF1A1C23, HOVER = 0xFF232631;
    private static final int LINE = 0xFF2B2E38, PURPLE = 0xFF7565FF;
    private static final int WHITE = 0xFFF0F0F4, MUTED = 0xFF858894;
    private static final String[] TABS = {"Modules", "Config", "Friends", "HUD"};
    private static final String[] CATEGORIES = {"All", "Combat", "Visuals", "Utility", "Movement", "Player", "Performance", "Misc"};

    private static final class Module {
        final String name, description, category;
        boolean enabled;
        Module(String name, String description, String category) {
            this.name=name; this.description=description; this.category=category;
        }
    }

    private final List<Module> modules = new ArrayList<>();
    private final boolean[] hudEnabled = {true, true, true, false, true};
    private int tab=0, category=0, scroll=0;
    private int panelX,panelY,panelW,panelH,contentX,contentY,contentW,contentH,cardW,rows;
    private final List<int[]> cardBounds = new ArrayList<>();
    private final int[] tabBounds = new int[4];
    private final int[] categoryBounds = new int[8];
    private final int[] configBounds = new int[3];
    private final int[] hudBounds = new int[5];
    private int selectedConfig=0;

    public ClearClickGui() {
        super(Text.literal("Clear Client"));
        add("Keystrokes HUD","Show your movement keys","Visuals");
        add("Coordinates","Display your position","Visuals");
        add("FPS Counter","Show current frame rate","Visuals");
        add("Toggle Sprint","Automatically maintain sprint","Movement");
        add("Armor Status","Display equipped armor","Visuals");
        add("Potion Effects","Show active status effects","Visuals");
        add("Direction HUD","Display facing direction","Visuals");
        add("Crosshair Customizer","Customize crosshair appearance","Visuals");
        add("Item Physics","Enhanced dropped item display","Visuals");
        add("Fullbright","Adjust world brightness","Visuals");
        add("Weather Visuals","Customize weather rendering","Visuals");
        add("Animations","Adjust hand and item animations","Visuals");
        add("Inventory Sort","Sort inventory items","Utility");
        add("Auto Tool","Select a suitable hotbar tool","Utility");
        add("Auto Eat","Food reminder utility","Utility");
        add("Mouse Wheelie","Inventory scrolling utility","Utility");
        add("Clean Chat","Reduce unwanted chat clutter","Utility");
        add("Quick Commands","Quick access to saved commands","Utility");
        add("Sprint","Convenient sprint control","Movement");
        add("Safe Walk","Sneak at block edges","Movement");
        add("View Bobbing","Customize view movement","Movement");
        add("Reach Display","Display interaction distance","Combat");
        add("Hit Color","Customize hit feedback","Combat");
        add("Totem Counter","Show held totem count","Combat");
        add("Armor HUD","Track nearby armor status","Combat");
        add("Ping Display","Show connection latency","Performance");
        add("FPS Optimizer","Client performance options","Performance");
        add("Particles","Customize particle display","Performance");
        add("Memory HUD","Display memory usage","Performance");
        add("World Time","Display world time","Misc");
        add("Clock","Display local system time","Misc");
        add("Screenshot Manager","Quick screenshot access","Misc");
    }

    private void add(String n,String d,String c){ modules.add(new Module(n,d,c)); }

    @Override
    public void render(DrawContext ctx,int mouseX,int mouseY,float delta) {
        ctx.fill(0,0,width,height,0x99000000);
        panelW=Math.min(790,width-24); panelH=Math.min(470,height-20);
        panelX=(width-panelW)/2; panelY=(height-panelH)/2;
        ctx.fill(panelX+4,panelY+4,panelX+panelW+4,panelY+panelH+4,0x55000000);
        ctx.fill(panelX,panelY,panelX+panelW,panelY+panelH,BG);
        ctx.fill(panelX,panelY,panelX+panelW,panelY+2,PURPLE);

        // Brand header
        ctx.drawText(textRenderer,"Clear",panelX+18,panelY+13,WHITE,false);
        ctx.drawText(textRenderer,"CLIENT",panelX+54,panelY+14,PURPLE,false);
        ctx.drawText(textRenderer,"1.21.11",panelX+panelW-62,panelY+15,MUTED,false);
        ctx.fill(panelX+14,panelY+36,panelX+panelW-14,panelY+37,LINE);

        // Main navigation
        int tx=panelX+16, ty=panelY+45;
        for(int i=0;i<TABS.length;i++){
            int tw= i==0?58: i==1?48: i==2?54:35;
            tabBounds[i]=new int[]{tx,ty-5,tx+tw,ty+19};
            boolean active=tab==i;
            if(active) ctx.fill(tx-5,ty-6,tx+tw+4,ty+20,0xFF242631);
            ctx.drawText(textRenderer,TABS[i],tx,ty,active?WHITE:MUTED,false);
            if(active) ctx.fill(tx-4,ty+20,tx+tw+3,ty+22,PURPLE);
            tx+=tw+19;
        }
        ctx.drawText(textRenderer,"●",panelX+panelW-35,ty,0xFF69C98A,false);

        contentX=panelX+16; contentY=panelY+83; contentW=panelW-32;
        contentH=panelH-119;
        if(tab==0) renderModules(ctx,mouseX,mouseY);
        else if(tab==1) renderConfig(ctx,mouseX,mouseY);
        else if(tab==2) renderFriends(ctx);
        else renderHud(ctx,mouseX,mouseY);

        ctx.fill(panelX+14,panelY+panelH-25,panelX+panelW-14,panelY+panelH-24,LINE);
        ctx.drawText(textRenderer,tab==0?"Click a module to toggle  •  Scroll to see more":
            "CLEAR CLIENT  •  Right Ctrl / F8 to close",panelX+17,panelY+panelH-16,MUTED,false);
        super.render(ctx,mouseX,mouseY,delta);
    }

    private void renderModules(DrawContext ctx,int mx,int my){
        int cx=contentX;
        for(int i=0;i<CATEGORIES.length;i++){
            int tw=textRenderer.getWidth(CATEGORIES[i])+14;
            categoryBounds[i]=new int[]{cx,contentY-3,cx+tw,contentY+17};
            boolean active=category==i;
            if(active) ctx.fill(cx,contentY-4,cx+tw,contentY+18,0xFF292B37);
            ctx.drawText(textRenderer,CATEGORIES[i],cx+7,contentY+3,active?WHITE:MUTED,false);
            if(active) ctx.fill(cx+6,contentY+17,cx+tw-6,contentY+18,PURPLE);
            cx+=tw+5;
        }
        int listY=contentY+31;
        int gap=9, columns=4;
        cardW=(contentW-gap*(columns-1))/columns;
        int cardH=66, rowGap=9;
        int visibleRows=Math.max(1,(contentH-35)/(cardH+rowGap));
        List<Module> filtered=new ArrayList<>();
        for(Module m:modules) if(category==0 || m.category.equals(CATEGORIES[category])) filtered.add(m);
        rows=(filtered.size()+columns-1)/columns;
        int maxScroll=Math.max(0,rows-visibleRows);
        scroll=Math.max(0,Math.min(scroll,maxScroll));
        cardBounds.clear();
        ctx.enableScissor(contentX,listY,contentX+contentW,contentY+contentH);
        for(int i=0;i<filtered.size();i++){
            Module m=filtered.get(i);
            int col=i%columns, row=i/columns-scroll;
            int x=contentX+col*(cardW+gap), y=listY+row*(cardH+rowGap);
            cardBounds.add(new int[]{x,y,x+cardW,y+cardH,modules.indexOf(m)});
            if(y+cardH<listY || y>contentY+contentH) continue;
            boolean hover=mx>=x&&mx<=x+cardW&&my>=y&&my<=y+cardH;
            ctx.fill(x,y,x+cardW,y+cardH,hover?HOVER:CARD);
            ctx.fill(x,y,x+cardW,y+1,m.enabled?PURPLE:LINE);
            // Tiny status icon and switch
            ctx.fill(x+10,y+10,x+18,y+18,0xFF343743);
            ctx.fill(x+12,y+12,x+16,y+16,m.enabled?PURPLE:MUTED);
            int sx=x+cardW-34, sy=y+10;
            ctx.fill(sx,sy,sx+25,sy+12,m.enabled?PURPLE:0xFF343741);
            int knob=m.enabled?sx+15:sx+2;
            ctx.fill(knob,sy+2,knob+8,sy+10,0xFFF4F4F7);
            ctx.drawText(textRenderer,m.name,x+10,y+37,WHITE,false);
            ctx.drawText(textRenderer,m.description,x+10,y+51,MUTED,false);
        }
        ctx.disableScissor();
        if(rows>visibleRows){
            String page=(scroll+1)+" / "+(maxScroll+1);
            ctx.drawText(textRenderer,page,contentX+contentW-textRenderer.getWidth(page),contentY+contentH-1,MUTED,false);
        }
    }

    private void renderConfig(DrawContext ctx,int mx,int my){
        ctx.drawText(textRenderer,"CONFIGURATION",contentX,contentY,WHITE,false);
        ctx.drawText(textRenderer,"Save and switch your client layouts",contentX,contentY+15,MUTED,false);
        String[] names={"Default","PvP Setup","Minimal"};
        String[] desc={"Balanced interface and modules","Combat-focused module layout","Reduced visual clutter"};
        for(int i=0;i<3;i++){
            int y=contentY+43+i*75, x=contentX, w=contentW;
            configBounds[i]=new int[]{x,y,x+w,y+64};
            ctx.fill(x,y,x+w,y+64,mx>=x&&mx<=x+w&&my>=y&&my<=y+64?HOVER:CARD);
            ctx.fill(x,y,x+3,y+64,selectedConfig==i?PURPLE:LINE);
            ctx.drawText(textRenderer,names[i],x+16,y+13,WHITE,false);
            ctx.drawText(textRenderer,desc[i],x+16,y+31,MUTED,false);
            if(selectedConfig==i) ctx.drawText(textRenderer,"ACTIVE",x+w-54,y+23,PURPLE,false);
            else ctx.drawText(textRenderer,"SELECT",x+w-55,y+23,MUTED,false);
        }
        ctx.drawText(textRenderer,"Profile selection is local to this screen for now.",contentX,contentY+280,MUTED,false);
    }

    private void renderFriends(DrawContext ctx){
        ctx.drawText(textRenderer,"FRIENDS",contentX,contentY,WHITE,false);
        ctx.drawText(textRenderer,"Your client-side friends list",contentX,contentY+16,MUTED,false);
        int y=contentY+48;
        ctx.fill(contentX,y,contentX+contentW,y+65,CARD);
        ctx.drawText(textRenderer,"No friends added",contentX+15,y+15,WHITE,false);
        ctx.drawText(textRenderer,"Friend management and presence indicators are planned next.",contentX+15,y+34,MUTED,false);
        ctx.drawText(textRenderer,"This section is open and ready for its own controls.",contentX, y+84,MUTED,false);
    }

    private void renderHud(DrawContext ctx,int mx,int my){
        ctx.drawText(textRenderer,"HUD EDITOR",contentX,contentY,WHITE,false);
        ctx.drawText(textRenderer,"Choose which on-screen overlays are shown",contentX,contentY+16,MUTED,false);
        String[] names={"Keystrokes","Coordinates","FPS Counter","Armor Status","Potion Effects"};
        for(int i=0;i<names.length;i++){
            int y=contentY+43+i*39, x=contentX, w=contentW;
            hudBounds[i]=new int[]{x,y,x+w,y+32};
            ctx.fill(x,y,x+w,y+32,mx>=x&&mx<=x+w&&my>=y&&my<=y+32?HOVER:CARD);
            ctx.drawText(textRenderer,names[i],x+12,y+12,WHITE,false);
            int sx=x+w-35, sy=y+10;
            ctx.fill(sx,sy,sx+24,sy+12,hudEnabled[i]?PURPLE:0xFF343741);
            int knob=hudEnabled[i]?sx+14:sx+2;
            ctx.fill(knob,sy+2,knob+8,sy+10,0xFFF4F4F7);
        }
    }

    @Override
    public boolean mouseClicked(Click click,boolean doubleClick){
        double mx=click.x(), my=click.y();
        if(click.button()==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            for(int i=0;i<4;i++) if(hit(tabBounds[i],mx,my)){tab=i;scroll=0;return true;}
            if(tab==0){
                for(int i=0;i<CATEGORIES.length;i++) if(hit(categoryBounds[i],mx,my)){category=i;scroll=0;return true;}
                for(int[] b:cardBounds) if(hit(b,mx,my)){int index=b[4];modules.get(index).enabled=!modules.get(index).enabled;return true;}
            } else if(tab==1){
                for(int i=0;i<3;i++) if(hit(configBounds[i],mx,my)){selectedConfig=i;return true;}
            } else if(tab==3){
                for(int i=0;i<5;i++) if(hit(hudBounds[i],mx,my)){hudEnabled[i]=!hudEnabled[i];return true;}
            }
        }
        return super.mouseClicked(click,doubleClick);
    }

    private boolean hit(int[] b,double x,double y){return b!=null&&x>=b[0]&&x<=b[2]&&y>=b[1]&&y<=b[3];}

    @Override
    public boolean mouseScrolled(double mouseX,double mouseY,double horizontalAmount,double verticalAmount){
        if(tab==0&&mouseX>=contentX&&mouseX<=contentX+contentW&&mouseY>=contentY){
            scroll-=verticalAmount>0?1:-1;
            return true;
        }
        return super.mouseScrolled(mouseX,mouseY,horizontalAmount,verticalAmount);
    }

    @Override
    public boolean shouldPause(){return false;}
}
