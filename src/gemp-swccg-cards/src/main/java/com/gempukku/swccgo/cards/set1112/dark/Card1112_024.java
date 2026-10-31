package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Character
 * Subtype: Alien
 * Title: Aurra Sing
 */
public class Card1112_024 extends AbstractAlien {
    public Card1112_024() {
        super(Side.DARK, 1, 4, 4, 4, 3, Title.Aurra_Sing, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Bounty hunter. Former student of the Force. After failing her Jedi training, Aurra became known for hunting down and killing Jedi Knights.");
        setGameText("During opponent's activate phase, may relocate Aurra to same site as a Jedi. Once per game, during your move phase, may fire her weapon at a Jedi; hit target is lost. Immune to attrition < 4.");
        addPersona(Persona.AURRA);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        final PhysicalCard here = self.getAtLocation();
        Filter siteWithJedi = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return Filters.site.accepts(gameState, modifiersQuerying, physicalCard)
                        && (here == null || physicalCard.getCardId() != here.getCardId())
                        && !Filters.filterActive(game, self, Filters.and(Filters.Jedi, Filters.at(physicalCard))).isEmpty();
            }
        };
        if (here != null
                && GameConditions.isDuringOpponentsPhase(game, self, Phase.ACTIVATE)
                && GameConditions.canTarget(game, self, siteWithJedi)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Relocate to a Jedi's site");
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose site with a Jedi", siteWithJedi) {
                        @Override
                        protected void cardSelected(PhysicalCard site) {
                            action.appendEffect(new RelocateBetweenLocationsEffect(action, self, site));
                        }
                    });
            actions.add(action);
        }

        final PhysicalCard weapon = Filters.findFirstActive(game, self, Filters.and(Filters.weapon, Filters.attachedTo(self)));
        if (weapon != null
                && GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.isOncePerGame(game, self, GameTextActionId.OTHER_CARD_ACTION_1)
                && GameConditions.canTarget(game, self, Filters.Jedi)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setText("Fire weapon at a Jedi");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new FireWeaponEffect(action, weapon, true, Filters.Jedi));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.Jedi, Filters.weapon, Filters.sameCardId(self))) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Lost hit Jedi");
            final PhysicalCard hit = ((com.gempukku.swccgo.logic.timing.results.HitResult) effectResult).getCardHit();
            action.appendEffect(new LoseCardFromTableEffect(action, hit));
            return Collections.singletonList(action);
        }
        return null;
    }
}
