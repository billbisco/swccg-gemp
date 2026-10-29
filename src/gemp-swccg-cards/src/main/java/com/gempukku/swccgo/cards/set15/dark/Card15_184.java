package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set11.dark.Card11_071;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

public class Card15_184 extends Card11_071 {
    public Card15_184() {
        setLore(""); setGameText("Deploy on table. Opponent may deploy Amidala from Reserve Deck; reshuffle. If Maul is present with Amidala and no Jedi, she is captured. Once per game may take If The Trace Was Correct into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        setAsHorizontal(true); setCredits(2);
        addIcon(Icon.TATOOINE,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "I Will Find Them Quickly Master"; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            PhysicalCard amidala = Filters.findFirstActive(game, self, Filters.Amidala);
            if (amidala != null) {
                if (GameConditions.canSpot(game, self, Filters.personaPresentAt(Filters.Maul, Filters.sameLocation(amidala)))
                    && !GameConditions.isWith(game, self, amidala, Filters.Jedi)) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setSingletonTrigger(true);
                    action.setText("Capture Amidala");
                    // Perform result(s)
                    action.appendEffect(
                            new CaptureCharacterOnTableEffect(action, amidala));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

}
