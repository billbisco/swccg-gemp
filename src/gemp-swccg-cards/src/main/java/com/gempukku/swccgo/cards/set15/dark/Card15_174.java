package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set14.dark.Card14_093;
import com.gempukku.swccgo.cards.set12.dark.Card12_130;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.*;
import com.gempukku.swccgo.filters.*;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.*;
public class Card15_174 extends Card14_093 {
    private static final LandingActions LANDING=new LandingActions();
    public Card15_174() {
        setLore(""); setGameText("Deploy on your Blockade Flagship (may not deploy on a site). Whenever you initiate battle at a related planet site where you have a battle droid, you may activate 2 Force or retrieve 1 Force. Your unique (•) Republic Characters are forfeit +2 and immune to Goo Nee Tay. Unless Imperial Arrest Order on table, once during your deploy phase, may deploy one docking bay from Reserve Deck; reshuffle.");
        addComboCardTitles("Begin Landing Your Troops",Title.Activate_The_Droids);
        addIcon(Icon.THEED_PALACE,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Begin Landing Your Troops & Activate The Droids"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) { return LANDING.modifiers(game,self); }
    @Override protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self,int textId) { return LANDING.actions(playerId,game,self,textId); }
    @Override protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int textId) { return null; }
    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        Filter filter = Filters.and(Filters.planet_site, Filters.relatedSite(self), Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.battle_droid)));

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, playerId, filter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Activate 2 Force or retrieve 1 Force");
            // Perform result(s)
            List<StandardEffect> effectChoices = new ArrayList<StandardEffect>();
            effectChoices.add(new ActivateForceEffect(action, playerId, 2));
            effectChoices.add(new RetrieveForceEffect(action, playerId, 1) {
                @Override
                public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                    return Filters.filterActive(game, null, Filters.and(Filters.your(self), Filters.participatingInBattle));
                }
                @Override
                public boolean isDueToInitiatingBattle() {
                    return true;
                }
            });
            action.appendEffect(
                    new ChooseEffectEffect(action, playerId, effectChoices));
            return Collections.singletonList(action);
        }
        return null;
    }
    private static final class LandingActions extends Card12_130 {
        List<Modifier> modifiers(SwccgGame game,PhysicalCard self) { return super.getGameTextWhileActiveInPlayModifiers(game,self); }
        List<TopLevelGameTextAction> actions(String playerId,SwccgGame game,PhysicalCard self,int textId) { return super.getGameTextTopLevelActions(playerId,game,self,textId); }
    }
}
