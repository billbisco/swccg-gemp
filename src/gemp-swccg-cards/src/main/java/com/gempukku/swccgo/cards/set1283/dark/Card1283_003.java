package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyLadyValarian;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Character
 * Subtype: Alien
 * Title: Lady Valarian
 */
public class Card1283_003 extends AbstractAlien {
    public Card1283_003() {
        super(Side.DARK, 2, 3, 3, 3, 5, LegacyLadyValarian.TITLE, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLore("Whiphid female gangster and leader.");
        setGameText("Your total battle destiny here is +1. Unless Jabba on table, 'Jabba' in the game text of your cards (except Hutt Bounty) may be treated as 'Lady Valarian.' Once per turn, may target a Mistryl of ability > 2 here; target gains Heroic until end of turn.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.FEMALE, Keyword.GANGSTER, Keyword.LEADER);
        setSpecies(Species.WHIPHID);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), 1, playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.not(Filters.Hutt_Bounty)),
                new UnlessCondition(new OnTableCondition(self, Filters.Jabba)),
                ModifyGameTextType.LEGACY__LADY_VALARIAN__JABBA_MAY_BE_TREATED_AS_LADY_VALARIAN));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.Mistryl, Filters.abilityMoreThan(2), Filters.here(self), Filters.canBeTargetedBy(self));

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make a Mistryl Heroic");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Mistryl", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " Heroic",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new KeywordModifier(self, Filters.sameCardId(finalTarget), Keyword.HEROIC),
                                                            "makes " + GameUtils.getCardLink(finalTarget) + " Heroic"));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
