package com.gempukku.swccgo.cards.set1107.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePurchasedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Character
 * Subtype: Droid
 * Title: Guri (V)
 */
public class Card1107_016 extends AbstractDroid {
    public Card1107_016() {
        super(Side.DARK, 2, 4, 6, 6, Title.Guri, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setArmor(5);
        setLore("Human-replica droid. Programmed to function as Xizor's personal bodyguard and assassin. Black Sun agent. Cost 9 million credits. Worth every decicred.");
        setGameText("Adds 2 to power of anything she pilots. At Coruscant sites where you have a Black Sun Agent, your Force drains are +1. While with Xizor in battle, she may be forfeited to satisfy all remaining battle damage against you. Immune to Restraining Bolt, purchase, and attrition < 5.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE, Keyword.BLACK_SUN_AGENT, Keyword.BODYGUARD, Keyword.ASSASSIN);
        addModelTypes(ModelType.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.Coruscant_site, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Black_Sun_agent))), 1, playerId));
        modifiers.add(new MayNotBePurchasedModifier(self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canForfeitToSatisfyBattleDamage(game, playerId, self)
                && GameConditions.isInBattleWith(game, self, Filters.Xizor)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Forfeit to satisfy all battle damage");
            action.appendCost(
                    new ForfeitCardFromTableEffect(action, self));
            action.setActionMsg(null);
            action.appendEffect(
                    new SatisfyAllBattleDamageEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
