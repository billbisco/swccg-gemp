package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReduceBattleDamageEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Security Breach, Virtual Block 6.2. */
public class Card1262_024 extends AbstractNormalEffect {
    public Card1262_024() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Security Breach", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("The Rebellion's limited resources force it to consider the wisdom of any military encounter. In many cases, retreat or deception is a preferable recourse.");
        setGameText("Deploy on table. Cancels game text of [Reflections 3] Naboo sites. Unless present with 3 other characters, Derdram's and Ephant Mon's game texts are canceled. Once per game, may place this Effect out of play to reduce your battle damage by 4. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter namedCharacters = Filters.and(Filters.character,
                Filters.or(Filters.title("Derdram"), Filters.title("Corporal Derdram"), Filters.Ephant_Mon));
        return Arrays.asList(new CancelsGameTextModifier(self, Filters.and(Filters.Naboo_site, Icon.REFLECTIONS_III)),
                new CancelsGameTextModifier(self, Filters.and(namedCharacters,
                        Filters.not(Filters.presentWith(self, 3, Filters.character)))));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId actionId = GameTextActionId.LEGACY__SECURITY_BREACH__REDUCE_BATTLE_DAMAGE;
        if (playerId.equals(self.getOwner())
                && TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isBattleDamageRemaining(game, playerId)
                && GameConditions.isOncePerGame(game, self, actionId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, actionId);
            action.setText("Place out of play to reduce battle damage by 4");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendCost(new PlaceCardOutOfPlayFromTableEffect(action, self));
            action.appendEffect(new ReduceBattleDamageEffect(action, playerId, 4));
            return Collections.singletonList(action);
        }
        return null;
    }
}
