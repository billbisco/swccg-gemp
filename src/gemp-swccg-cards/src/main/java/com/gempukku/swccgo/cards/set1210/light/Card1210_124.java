package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Faithful Service
 */
public class Card1210_124 extends AbstractNormalEffect {
    public Card1210_124() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Faithful Service", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Unlike the Empire, the Alliance treats their droids with respect. Many droids volunteered to share the risk of battle and aid the Rebellion's assault on the Death Star.");
        setGameText("Deploy on table. Your unique (•) [Hoth], [Endor], and [Death Star II] non-pilot warriors of destiny > 1 are troopers. Your troopers have armor = 4. When opponent is about to remove their last card from Life Force, you may first place all your troopers in Used Pile (last card must still be removed). (Immune to Alter.)");
        addIcons(Icon.PREMIUM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter uniqueSetWarriors = Filters.and(Filters.your(self), Filters.unique, Filters.or(Icon.HOTH, Icon.ENDOR, Icon.DEATH_STAR_II),
                Filters.warrior, Filters.not(Filters.pilot), Filters.not(Filters.destinyLessThanOrEqualTo(1)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, uniqueSetWarriors, Keyword.TROOPER));
        modifiers.add(new ResetArmorModifier(self, Filters.and(Filters.your(self), Filters.trooper), 4));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isAboutToLoseForce(game, effectResult, opponent)) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;
            if (game.getGameState().getPlayerLifeForce(opponent) <= result.getForceLossAmount(game)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.trooper))) {
                Collection<PhysicalCard> troopers = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.trooper));
                if (!troopers.isEmpty()) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Place troopers in Used Pile");
                    action.appendEffect(
                            new PlaceCardsInUsedPileFromTableEffect(action, troopers));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
