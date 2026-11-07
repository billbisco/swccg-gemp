package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.Collections;
import java.util.List;

/** Legacy Block6.1: Kir Kanos With Force Pike. */
public class Card1261_011 extends AbstractImperial {
    public Card1261_011() {
        super(Side.DARK, 3, 3, 4, 2, 4, "Kir Kanos With Force Pike", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Fiercely devoted Royal Guard. Feels deeply indebted to those who risk their life for him. Unaware of the extent of Palpatine's atrocities and cruelty.");
        setGameText("Heroic. Permanent weapon is Force Pike (may target a character for free; draw destiny; target hit, and its power = 0, if destiny +1 > defense value). Your characters here are immune to Cantina Brawl, Fallen Portal, and Rebel Barrier.");
        addIcons(Icon.WARRIOR, Icon.PERMANENT_WEAPON, Icon.REFLECTIONS_II);
        addKeywords(Keyword.ROYAL_GUARD, Keyword.HEROIC);
        addPersona(Persona.KIR_KANOS);
    }

    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        AbstractPermanentWeapon weapon = new AbstractPermanentWeapon("Force Pike") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self,
                    boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring,
                    Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter targetFilter, boolean ignoreLimit) {
                FireWeaponActionBuilder builder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self,
                        this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter,
                        targetFilter, ignoreLimit).targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.TO_BE_HIT)
                        .finishBuildPrep();
                // Existing Force Pike action resets power when wielded by a Royal Guard, including this permanent weapon.
                return builder == null ? null : Collections.singletonList(builder.buildFireWeaponForcePikeAction());
            }
        };
        weapon.addKeyword(Keyword.FORCE_PIKE);
        return weapon;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter charactersHere = Filters.and(Filters.your(self), Filters.character, Filters.here(self));
        return List.of(new ImmuneToTitleModifier(self, charactersHere, Title.Cantina_Brawl),
                new ImmuneToTitleModifier(self, charactersHere, Title.Fallen_Portal),
                new ImmuneToTitleModifier(self, charactersHere, Title.Rebel_Barrier));
    }
}
