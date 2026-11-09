package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PoliticsModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/**
 * Legacy Block 5: Keder The Black (V). Text = PC-E1, the PC website card list (by 2012-07-21): the closing quotation mark
 * after 'senators' is dropped and "Squabbling" is spelled "Squabling" (published typo kept; play unchanged, the card still
 * affects Squabbling Delegates). The release-sheet original is 2250_9.
 */
public class Card1250_009 extends AbstractRepublic {
    public Card1250_009() {
        super(Side.DARK, 1, 3, 4, 3, 6, "Keder The Black", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("A highly paid assassin and spy, Keder has infiltrated the Senate disguised as a Coruscant Guard. The identity of his target and his employer remains a mystery to all but him.");
        setGameText("While at Senate: Sidious is politics +3; 'senators on Squabling Delegates is treated as 'Coruscant Guards;' and may lose 1 Force to place Keder and an opponent's [Coruscant] Political Effect (or [Coruscant] senator here) in owner's Lost Pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.CORUSCANT_GUARD, Keyword.ASSASSIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var atSenate = new AtCondition(self, Filters.Galactic_Senate);
        return List.of(new PoliticsModifier(self, Filters.Sidious, atSenate, 3),
                new ModifyGameTextModifier(self, Filters.title("Squabbling Delegates"), atSenate,
                        ModifyGameTextType.LEGACY__SQUABBLING_DELEGATES__SENATORS_ARE_CORUSCANT_GUARDS));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        Filter target = Filters.and(Filters.opponents(self), Icon.CORUSCANT,
                Filters.or(Filters.Political_Effect, Filters.and(Filters.senator, Filters.here(self))));
        if (!GameConditions.isAtLocation(game, self, Filters.Galactic_Senate)
                || !GameConditions.canTarget(game, self, TargetingReason.TO_BE_LOST, target)) {
            return null;
        }
        var action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Lose Keder and opposing Political Effect or senator");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose opposing Coruscant Political Effect or senator here",
                TargetingReason.TO_BE_LOST, target) {
            @Override
            protected void cardTargeted(int targetGroupId, PhysicalCard selected) {
                action.appendCost(new LoseForceEffect(action, playerId, 1, true));
                action.allowResponses("Make Keder and the selected card lost", new RespondableEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        var finalTarget = action.getPrimaryTargetCard(targetGroupId);
                        action.appendEffect(new LoseCardsFromTableSimultaneouslyEffect(action,
                                List.of(self, finalTarget), false, true));
                    }
                });
            }
        });
        return List.of(action);
    }
}
