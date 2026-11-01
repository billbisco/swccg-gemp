package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ApplyAbilityToDrawBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: ISB Sector Office
 */
public class Card1113_019 extends AbstractSite {
    public Card1113_019() {
        super(Side.DARK, "ISB Sector Office", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setGameText("Dark: Your total weapon and battle destiny here is + X, where X = highest printed influence at Conference Room. At related sites, may apply the ability of one Imperial Council Member here toward drawing battle destiny. Light: May not deploy to Bespin, Dagobah, Endor, Hoth, Kashyyyk, Ralltiir or Yavin 4.");
        setLocationDarkSideGameText("Your total weapon and battle destiny here is + X, where X = highest printed influence at Conference Room. At related sites, may apply the ability of one Imperial Council Member here toward drawing battle destiny.");
        setLocationLightSideGameText("May not deploy to Bespin, Dagobah, Endor, Hoth, Kashyyyk, Ralltiir or Yavin 4.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addMayNotBePartOfSystem(Title.Bespin, Title.Dagobah, Title.Endor, Title.Hoth, Title.Kashyyyk, Title.Ralltiir, Title.Yavin_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Evaluator highestPrinted = new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                PhysicalCard room = Filters.findFirstActive(gameState.getGame(), self, Filters.Death_Star_Conference_Room);
                if (room == null) {
                    return 0;
                }
                return modifiersQuerying.getHighestPrintedInfluenceAtLocation(gameState, room);
            }

            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return evaluateExpression(gameState, modifiersQuerying, cardAffected);
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.here(self)), Filters.your(playerOnDarkSideOfLocation), highestPrinted, Filters.any));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), highestPrinted, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter councilMemberHere = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character,
                Keyword.IMPERIAL_COUNCIL_MEMBER, Filters.here(self), Filters.abilityMoreThan(0));
        if (GameConditions.isDuringBattleAt(game, Filters.relatedSite(self))
                && GameConditions.isOncePerBattle(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, councilMemberHere)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Apply a Council Member's ability");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerOnDarkSideOfLocation, "Choose an Imperial Council Member",
                            councilMemberHere) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses(
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new ApplyAbilityToDrawBattleDestinyEffect(action, playerOnDarkSideOfLocation, targetedCard));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
