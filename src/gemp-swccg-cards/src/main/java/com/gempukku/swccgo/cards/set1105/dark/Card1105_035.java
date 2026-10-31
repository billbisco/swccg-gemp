package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseTopCardOfReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Effect
 * Title: Desilijic Tattoo
 */
public class Card1105_035 extends AbstractNormalEffect {
    public Card1105_035() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Desilijic Tattoo", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba The Hutt's clan requires all members to carry ritual scarring identifying them forever as heirs to the Nal Hutta legacy. These tattoos are considered marks of power.");
        setGameText("Deploy on table. Bounty Hunters and your Independent starships are defense value +1 and forfeit +2. Once per turn, when a bounty hunter uses a non-lightsaber weapon to hit a character, target is forfeit -2 and opponent places top card of Reserve Deck in Lost Pile. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter affected = Filters.or(Filters.bounty_hunter, Filters.and(Filters.your(self), Filters.starship, Icon.INDEPENDENT));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, affected, 1));
        modifiers.add(new ForfeitModifier(self, affected, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.justHitBy(game, effectResult, Filters.character, Filters.and(Filters.weapon, Filters.not(Filters.lightsaber)), Filters.bounty_hunter)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            final PhysicalCard hitCard = ((HitResult) effectResult).getCardHit();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce forfeit and lose top of Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new ForfeitModifier(self, Filters.sameCardId(hitCard), -2),
                            "makes " + hitCard.getTitle() + " forfeit -2"));
            action.appendEffect(
                    new LoseTopCardOfReserveDeckEffect(action, opponent));
            return Collections.singletonList(action);
        }
        return null;
    }
}
