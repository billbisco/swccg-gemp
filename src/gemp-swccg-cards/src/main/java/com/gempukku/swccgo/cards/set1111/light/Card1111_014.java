package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Effect
 * Title: Plastoid Armor
 */
public class Card1111_014 extends AbstractNormalEffect {
    public Card1111_014() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Plastoid_Armor, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("Luke and Han took the armor suits from fallen stormtroopers and used them as both protection and disguise to penetrate the detention cell block aboard the Death Star.");
        setGameText("Deploy on your non-droid character. Your Interrupts with 'back' in title are destiny = 5 and are Used Interrupts. Your characters are immune to Bad Feeling Have I. During opponent's move phase, may take an Interrupt with 'back' in title into hand from Reserve Deck; reshuffle.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.droid));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.and(Filters.character, Filters.not(Filters.droid));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourBackInterrupts = Filters.and(Filters.your(self), Filters.Interrupt, Filters.titleContains("back"));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetDestinyModifier(self, yourBackInterrupts, 5));
        modifiers.add(new UsedInterruptModifier(self, yourBackInterrupts));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.character), Title.Bad_Feeling_Have_I));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__PLASTOID_ARMOR_V__TAKE_INTERRUPT;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.MOVE)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Interrupt with 'back' into hand");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Interrupt, Filters.titleContains("back")), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
