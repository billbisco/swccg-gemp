package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Effect
 * Title: What About That Blue One?
 */
public class Card1291_054 extends AbstractNormalEffect {
    public Card1291_054() {
        super(Side.LIGHT, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "What About That Blue One?", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("\"You haven't finished with me yet!\"");
        setGameText("Deploy on table. Your total Force generation is +1. Once per game may take an astromech into hand from Reserve Deck; reshuffle. Fire Extinguisher may only cancel battles you initiate and [Virtual Block 4] Wokling is canceled. Once per game may retrieve a Rebel of ability < 5 or a Rebel starfighter. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    static Filter vb4Wokling() {
        return Filters.and(Filters.title(Title.Wokling), Icon.LEGACY_BLOCK_4);
    }

    static Filter rebelToRetrieve() {
        return Filters.or(Filters.and(Filters.Rebel, Filters.character, Filters.abilityLessThan(5)), Filters.Rebel_starfighter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalForceGenerationModifier(self, 1, self.getOwner()));
        modifiers.add(new ModifyGameTextModifier(self, Filters.title(Title.Fire_Extinguisher),
                ModifyGameTextType.LEGACY__WHAT_ABOUT_THAT_BLUE_ONE__FIRE_EXTINGUISHER_ONLY_YOUR_BATTLES));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId uploadId = GameTextActionId.LEGACY__WHAT_ABOUT_THAT_BLUE_ONE__UPLOAD_ASTROMECH;
        if (GameConditions.isOncePerGame(game, self, uploadId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, uploadId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, uploadId);
            action.setText("Take an astromech into hand from Reserve Deck");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.astromech_droid, true));
            actions.add(action);
        }

        GameTextActionId retrieveId = GameTextActionId.LEGACY__WHAT_ABOUT_THAT_BLUE_ONE__RETRIEVE_REBEL;
        if (GameConditions.isOncePerGame(game, self, retrieveId)
                && GameConditions.hasLostPile(game, playerId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, retrieveId);
            action.setText("Retrieve a Rebel of ability < 5 or a Rebel starfighter");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new RetrieveCardEffect(action, playerId, rebelToRetrieve()));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, vb4Wokling())
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, vb4Wokling())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, vb4Wokling(), Title.Wokling);
            return Collections.singletonList(action);
        }
        return null;
    }
}
