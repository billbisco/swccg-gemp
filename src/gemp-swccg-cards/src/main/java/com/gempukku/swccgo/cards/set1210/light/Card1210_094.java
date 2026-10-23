package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageAndAttritionEffect;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Rebel
 * Title: Chewie, Enraged
 */
public class Card1210_094 extends AbstractRebel {
    public Card1210_094() {
        super(Side.LIGHT, 3, 4, 6, 2, 6, "Chewie, Enraged", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("Smuggler. Very few people need to be told that angry Wookiees should be given a wide berth.");
        setGameText("Adds 2 to power of anything he pilots. While present at a site with Han or Leia, Chewie is Heroic and, unless hit, may be forfeited to satisfy all remaining attrition and battle damage against you. Once per game, may take Kashyyyk or Falcon into hand from Reserve Deck; reshuffle.");
        addPersona(Persona.CHEWIE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.REFLECTIONS_III);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.WOOKIEE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        AndCondition presentAtSiteWithHanOrLeia = new AndCondition(new PresentAtCondition(self, Filters.site),
                new PresentWithCondition(self, Filters.or(Filters.Han, Filters.Leia)));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new KeywordModifier(self, presentAtSiteWithHanOrLeia, Keyword.HEROIC));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CHEWIE_ENRAGED_V__TAKE_KASHYYYK_OR_FALCON;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Kashyyyk or Falcon into hand from Reserve Deck");
            action.setActionMsg("Take Kashyyyk or Falcon into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Kashyyyk_system, Filters.Falcon), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canForfeitToSatisfyAttritionAndBattleDamage(game, playerId, self)
                && GameConditions.isPresentAt(game, self, Filters.site)
                && GameConditions.isPresentWith(game, self, Filters.or(Filters.Han, Filters.Leia))
                && !GameConditions.isHit(game, self)) {
            boolean cannotSatisfyAttrition = game.getModifiersQuerying().cannotSatisfyAttrition(game.getGameState(), self);

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            if (cannotSatisfyAttrition)
                action.setText("Forfeit to satisfy all battle damage");
            else
                action.setText("Forfeit to satisfy all remaining attrition and battle damage");
            action.appendCost(
                    new ForfeitCardFromTableEffect(action, self));
            action.setActionMsg(null);
            if (cannotSatisfyAttrition)
                action.appendEffect(
                        new SatisfyAllBattleDamageEffect(action, playerId));
            else
                action.appendEffect(
                        new SatisfyAllBattleDamageAndAttritionEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
