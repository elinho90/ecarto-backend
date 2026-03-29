import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch

# ── Palette ──────────────────────────────────────────────────────────────────
COLOR_TABLE_HDR     = '#1E3A5F'
COLOR_TABLE_HDR_TXT = 'white'
COLOR_VIEW_HDR      = '#0D6E6E'
COLOR_VIEW_HDR_TXT  = 'white'
COLOR_ROW_ODD       = '#F0F4F8'
COLOR_ROW_EVEN      = 'white'
COLOR_PK            = '#DCEEFB'
COLOR_FK            = '#FFF0DD'
COLOR_BORDER        = '#90A4AE'
COLOR_BG            = '#FFFFFF'
COLOR_REL           = '#37474F'
COLOR_REL_VIEW      = '#2E7D32'

# ── Sizing (optimised for report print) ──────────────────────────────────────
ROW_H   = 1.45       # taller rows
HDR_H   = 2.1        # taller header
COL_W   = 22.0       # wider tables to accommodate more info
FONT_SZ = 9.5        # base font – very readable at print
HDR_FSZ = 11.0       # header font
TYPE_FSZ = 8.5       # type annotations

ENTITIES = {
    'utilisateurs': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('email', 'VARCHAR(254)', ''),
            ('password', 'VARCHAR(254)', ''),
            ('nom', 'VARCHAR(100)', ''),
            ('prenom', 'VARCHAR(100)', ''),
            ('role', 'ENUM', ''),
            ('telephone', 'VARCHAR(20)', ''),
            ('departement', 'VARCHAR(100)', ''),
            ('poste', 'VARCHAR(100)', ''),
            ('actif', 'BOOLEAN', ''),
            ('refresh_token', 'VARCHAR(500)', ''),
            ('refresh_token_expiry', 'TIMESTAMP', ''),
            ('dashboard_config', 'TEXT', ''),
            ('created_at', 'TIMESTAMP', ''),
            ('updated_at', 'TIMESTAMP', ''),
        ]
    },
    'notifications': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('utilisateur_id', 'BIGINT', 'fk'),
            ('type', 'VARCHAR', ''),
            ('titre', 'VARCHAR(200)', ''),
            ('message', 'TEXT', ''),
            ('lu', 'BOOLEAN', ''),
            ('created_at', 'TIMESTAMP', ''),
        ]
    },
    'type_projet': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('nom', 'VARCHAR(100)', ''),
            ('description', 'TEXT', ''),
            ('libelle', 'VARCHAR', ''),
            ('couleur', 'VARCHAR(7)', ''),
            ('icone', 'VARCHAR(50)', ''),
            ('est_actif', 'BOOLEAN', ''),
            ('created_at', 'TIMESTAMP', ''),
            ('updated_at', 'TIMESTAMP', ''),
        ]
    },
    'sites': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('nom', 'VARCHAR', ''),
            ('adresse', 'VARCHAR', ''),
            ('ville', 'VARCHAR', ''),
            ('region', 'VARCHAR', ''),
            ('pays', 'VARCHAR', ''),
            ('latitude', 'DOUBLE', ''),
            ('longitude', 'DOUBLE', ''),
            ('type', 'ENUM', ''),
            ('statut', 'ENUM', ''),
            ('created_at', 'TIMESTAMP', ''),
            ('updated_at', 'TIMESTAMP', ''),
        ]
    },
    'projets': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('nom', 'VARCHAR(100)', ''),
            ('description', 'TEXT', ''),
            ('statut', 'ENUM', ''),
            ('priorite', 'ENUM', ''),
            ('responsable', 'VARCHAR(100)', ''),
            ('date_debut', 'DATE', ''),
            ('date_fin_prevue', 'DATE', ''),
            ('date_fin_reelle', 'DATE', ''),
            ('budget', 'DECIMAL(12,2)', ''),
            ('progression', 'INTEGER', ''),
            ('type_projet_id', 'BIGINT', 'fk'),
            ('site_id', 'BIGINT', 'fk'),
            ('tags', 'VARCHAR(500)', ''),
            ('created_at', 'TIMESTAMP', ''),
            ('updated_at', 'TIMESTAMP', ''),
        ]
    },
    'projet_equipe': {
        'is_view': False,
        'cols': [
            ('projet_id', 'BIGINT', 'pk,fk'),
            ('membre', 'VARCHAR(100)', 'pk'),
        ]
    },
    'rapports': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('nom', 'VARCHAR(200)', ''),
            ('fichier_nom', 'VARCHAR(255)', ''),
            ('fichier_type', 'VARCHAR(20)', ''),
            ('fichier_chemin', 'VARCHAR(500)', ''),
            ('projet_id', 'BIGINT', 'fk'),
            ('uploade_par', 'VARCHAR(100)', ''),
            ('faisabilite', 'INTEGER', ''),
            ('risque', 'ENUM', ''),
            ('budget_estime', 'DECIMAL', ''),
            ('duree_estimee_mois', 'INTEGER', ''),
            ('created_at', 'TIMESTAMP', ''),
        ]
    },
    'tasks': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('title', 'VARCHAR', ''),
            ('description', 'TEXT', ''),
            ('statut', 'ENUM', ''),
            ('display_order', 'INTEGER', ''),
            ('projet_id', 'BIGINT', 'fk'),
            ('created_at', 'TIMESTAMP', ''),
        ]
    },
    'parametres_systeme': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('cle', 'VARCHAR(100)', ''),
            ('valeur', 'TEXT', ''),
            ('description', 'TEXT', ''),
            ('created_at', 'TIMESTAMP', ''),
        ]
    },
    'vue_statistiques_projets': {
        'is_view': True,
        'cols': [
            ('total_projets', 'BIGINT', ''),
            ('projets_en_cours', 'BIGINT', ''),
            ('projets_termines', 'BIGINT', ''),
            ('budget_total', 'NUMERIC', ''),
            ('progression_moyenne', 'NUMERIC', ''),
        ]
    },
    'phases': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('nom', 'VARCHAR(150)', ''),
            ('description', 'TEXT', ''),
            ('ordre', 'INTEGER', ''),
            ('projet_id', 'BIGINT', 'fk'),
            ('date_debut_prevue', 'DATE', ''),
            ('date_fin_prevue', 'DATE', ''),
            ('date_debut_reelle', 'DATE', ''),
            ('date_fin_reelle', 'DATE', ''),
            ('progression', 'INTEGER', ''),
            ('statut', 'ENUM', ''),
            ('verrouillee', 'BOOLEAN', ''),
        ]
    },
    'etapes': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('nom', 'VARCHAR(200)', ''),
            ('description', 'TEXT', ''),
            ('ordre', 'INTEGER', ''),
            ('phase_id', 'BIGINT', 'fk'),
            ('responsable_id', 'BIGINT', 'fk'),
            ('date_echeance', 'DATE', ''),
            ('date_realisation', 'DATE', ''),
            ('duree_estimee', 'INTEGER', ''),
            ('duree_reelle', 'INTEGER', ''),
            ('statut', 'ENUM', ''),
            ('validation_requise', 'BOOLEAN', ''),
            ('bloquante', 'BOOLEAN', ''),
        ]
    },
    'validations_etapes': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('etape_id', 'BIGINT', 'fk'),
            ('validateur_id', 'BIGINT', 'fk'),
            ('decision', 'ENUM', ''),
            ('commentaire', 'TEXT', ''),
            ('date_validation', 'TIMESTAMP', ''),
            ('pieces_jointes', 'TEXT', ''),
        ]
    },
    'risques': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('projet_id', 'BIGINT', 'fk'),
            ('titre', 'VARCHAR', ''),
            ('description', 'TEXT', ''),
            ('probabilite', 'ENUM', ''),
            ('impact', 'ENUM', ''),
            ('plan_mitigation', 'TEXT', ''),
            ('statut', 'ENUM', ''),
            ('responsable_id', 'BIGINT', 'fk'),
            ('date_identification', 'DATE', ''),
            ('date_resolution', 'DATE', ''),
        ]
    },
    'alertes': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('projet_id', 'BIGINT', 'fk'),
            ('etape_id', 'BIGINT', 'fk'),
            ('type', 'ENUM', ''),
            ('niveau', 'ENUM', ''),
            ('message', 'TEXT', ''),
            ('lue', 'BOOLEAN', ''),
            ('resolue', 'BOOLEAN', ''),
            ('destinataire_id', 'BIGINT', 'fk'),
            ('created_at', 'TIMESTAMP', ''),
        ]
    },
    'historique_statuts': {
        'is_view': False,
        'cols': [
            ('id', 'BIGINT', 'pk'),
            ('projet_id', 'BIGINT', 'fk'),
            ('statut_avant', 'ENUM', ''),
            ('statut_apres', 'ENUM', ''),
            ('utilisateur_id', 'BIGINT', 'fk'),
            ('motif', 'TEXT', ''),
            ('date_changement', 'TIMESTAMP', ''),
        ]
    }
}

# ── Layout positions (x, y = top-left corner of each table) ──────────────────
# A larger grid for clarity: 0 to 140 width, 0 to 180 height
LAYOUT = {
    'validations_etapes':        (10,  175),
    'etapes':                    (40,  175),
    'phases':                    (70,  175),
    'sites':                     (100, 175),
    
    'alertes':                   (10,  135),
    'utilisateurs':              (40,  135),
    'projets':                   (70,  135),
    'risques':                   (100, 135),
    
    'notifications':             (10,  85),
    'type_projet':               (40,  85),
    'projet_equipe':             (100, 85),
    
    'rapports':                  (70,  70),
    
    'parametres_systeme':        (10,  40),
    'historique_statuts':        (40,  40),
    'tasks':                     (100, 40),

    'vue_statistiques_projets':  (40,  10),
    'vue_statistiques_rapports': (70,  10),
}


# ── Helper functions ─────────────────────────────────────────────────────────
def table_height(name):
    return HDR_H + len(ENTITIES[name]['cols']) * ROW_H

def get_center(name):
    x, y = LAYOUT[name]
    return x + COL_W / 2, y - table_height(name) / 2

def get_bottom(name):
    x, y = LAYOUT[name]
    return x + COL_W / 2, y - table_height(name)

def get_top(name):
    x, y = LAYOUT[name]
    return x + COL_W / 2, y

def get_right(name):
    x, y = LAYOUT[name]
    return x + COL_W, y - table_height(name) / 2

def get_left(name):
    x, y = LAYOUT[name]
    return x, y - table_height(name) / 2


def draw_table(ax, name):
    info = ENTITIES[name]
    is_view = info['is_view']
    cols = info['cols']
    x, y_top = LAYOUT[name]

    hdr_color = COLOR_VIEW_HDR if is_view else COLOR_TABLE_HDR
    hdr_txt   = COLOR_VIEW_HDR_TXT if is_view else COLOR_TABLE_HDR_TXT
    h = table_height(name)

    # Shadow
    ax.add_patch(FancyBboxPatch(
        (x + 0.3, y_top - h - 0.3), COL_W, h,
        boxstyle="round,pad=0.1", linewidth=0,
        facecolor='#B0BEC5', alpha=0.35, zorder=1))

    # Body
    ax.add_patch(plt.Rectangle(
        (x, y_top - h), COL_W, h,
        linewidth=1.8, linestyle=('--' if is_view else '-'),
        edgecolor=COLOR_BORDER, facecolor='white', zorder=2))

    # Header band
    ax.add_patch(plt.Rectangle(
        (x, y_top - HDR_H), COL_W, HDR_H,
        linewidth=0, facecolor=hdr_color, zorder=3))

    # Table name
    display = name
    if is_view:
        display = f'<<view>>  {name}'
    ax.text(x + COL_W / 2, y_top - HDR_H / 2, display,
            ha='center', va='center', fontsize=HDR_FSZ, color=hdr_txt,
            fontweight='bold', zorder=4, fontfamily='sans-serif')

    # Column rows
    for i, (col, typ, flags) in enumerate(cols):
        ry = y_top - HDR_H - i * ROW_H
        if 'pk' in flags and 'fk' in flags:
            bg = '#E8D5FF'
        elif 'pk' in flags:
            bg = COLOR_PK
        elif 'fk' in flags:
            bg = COLOR_FK
        else:
            bg = COLOR_ROW_ODD if i % 2 == 0 else COLOR_ROW_EVEN

        ax.add_patch(plt.Rectangle(
            (x, ry - ROW_H), COL_W, ROW_H,
            linewidth=0, facecolor=bg, zorder=3))

        # Thin separator
        ax.plot([x, x + COL_W], [ry - ROW_H, ry - ROW_H],
                color=COLOR_BORDER, linewidth=0.35, zorder=4)

        # Prefix tag
        if 'pk' in flags and 'fk' in flags:
            prefix = 'PK/FK '
            col_color = '#6A1B9A'
        elif 'pk' in flags:
            prefix = 'PK '
            col_color = '#0D47A1'
        elif 'fk' in flags:
            prefix = 'FK '
            col_color = '#BF360C'
        else:
            prefix = '     '
            col_color = '#212121'

        fw = 'bold' if 'pk' in flags else 'normal'
        ax.text(x + 0.5, ry - ROW_H / 2, f'{prefix}{col}',
                ha='left', va='center', fontsize=FONT_SZ, color=col_color,
                fontweight=fw, zorder=5, fontfamily='sans-serif')
        ax.text(x + COL_W - 0.5, ry - ROW_H / 2, typ,
                ha='right', va='center', fontsize=TYPE_FSZ, color='#546E7A',
                zorder=5, fontfamily='sans-serif')


def draw_relation(ax, src, dst, is_view_rel=False):
    scx, scy = get_center(src)
    dcx, dcy = get_center(dst)

    color = COLOR_REL_VIEW if is_view_rel else COLOR_REL
    ls = (0, (6, 4)) if is_view_rel else '-'

    # Decide which edges to connect
    if abs(scx - dcx) >= abs(scy - dcy):
        if scx < dcx:
            x1, y1 = get_right(src)
            x2, y2 = get_left(dst)
        else:
            x1, y1 = get_left(src)
            x2, y2 = get_right(dst)
    else:
        if scy > dcy:
            x1, y1 = get_bottom(src)
            x2, y2 = get_top(dst)
        else:
            x1, y1 = get_top(src)
            x2, y2 = get_bottom(dst)

    ax.annotate('',
                xy=(x2, y2), xytext=(x1, y1),
                arrowprops=dict(
                    arrowstyle='-|>', color=color, lw=2.0,
                    linestyle=ls,
                    connectionstyle='arc3,rad=0.06',
                    mutation_scale=18,
                ), zorder=1)

    # "1" label near source
    mx = x1 + (x2 - x1) * 0.08
    my = y1 + (y2 - y1) * 0.08
    ax.text(mx, my, ' 1', fontsize=9, fontweight='bold', color=color,
            ha='center', va='center', zorder=2)

    # "*" label near target
    nx = x2 - (x2 - x1) * 0.08
    ny = y2 - (y2 - y1) * 0.08
    ax.text(nx, ny, ' *', fontsize=9, fontweight='bold', color=color,
            ha='center', va='center', zorder=2)


# ── Main Drawing ─────────────────────────────────────────────────────────────
RELATIONS = [
    ('utilisateurs', 'notifications', False),
    ('type_projet',  'projets',       False),
    ('sites',        'projets',       False),
    ('projets',      'projet_equipe', False),
    ('projets',      'rapports',      False),
    ('projets',      'tasks',         False),
    ('projets',      'vue_statistiques_projets',  True),
    ('rapports',     'vue_statistiques_rapports', True),
    # New relationships!
    ('projets',      'phases',        False),
    ('phases',       'etapes',        False),
    ('etapes',       'validations_etapes', False),
    ('utilisateurs', 'validations_etapes', False),
    ('utilisateurs', 'etapes',        False),
    ('projets',      'risques',       False),
    ('utilisateurs', 'risques',       False),
    ('projets',      'alertes',       False),
    ('etapes',       'alertes',       False),
    ('utilisateurs', 'alertes',       False),
    ('projets',      'historique_statuts', False),
    ('utilisateurs', 'historique_statuts', False),
]

# Increase canvas size significantly
fig, ax = plt.subplots(figsize=(55, 45), dpi=250)
fig.patch.set_facecolor(COLOR_BG)
ax.set_facecolor(COLOR_BG)
ax.set_xlim(-2, 130)
ax.set_ylim(-5, 185)
ax.axis('off')

# Title
ax.text(65, 182, 'E-Carto  -  Diagramme de Bases de Données (UML Complet)',
        ha='center', va='center', fontsize=26, fontweight='bold',
        color='#1E3A5F', fontfamily='sans-serif')
ax.text(65, 180, 'Généré automatiquement - Inclut les nouveaux modules (Phases, Risques, Alertes) et processus',
        ha='center', va='center', fontsize=16, color='#78909C', fontfamily='sans-serif')

# Draw relations
for src, dst, is_v in RELATIONS:
    try:
        draw_relation(ax, src, dst, is_view_rel=is_v)
    except Exception as e:
        print("Relation error:", src, dst, e)

# Draw all tables
for name in ENTITIES:
    draw_table(ax, name)

# ── Legend ────────────────────────────────────────────────────────────────────
lx, ly = 100, 15
ax.add_patch(plt.Rectangle((lx, ly - 8), 24, 8.5,
             facecolor='white', edgecolor=COLOR_BORDER, linewidth=1.2, zorder=5))
ax.text(lx + 12, ly + 0.2, 'Légende', ha='center', fontsize=14,
        fontweight='bold', color='#1E3A5F', zorder=6)

items = [
    (COLOR_PK,    '#0D47A1', 'Clé primaire (PK)'),
    (COLOR_FK,    '#BF360C', 'Clé étrangère (FK)'),
    ('#E8D5FF',   '#6A1B9A', 'PK + FK'),
    (COLOR_ROW_ODD, '#212121', 'Colonne normale'),
]
for j, (bg, fc, label) in enumerate(items):
    yy = ly - 1.3 - j * 1.3
    ax.add_patch(plt.Rectangle((lx + 0.6, yy - 0.45), 2.5, 0.9,
                                facecolor=bg, edgecolor=COLOR_BORDER, lw=0.6, zorder=6))
    ax.text(lx + 3.8, yy, label, va='center', fontsize=11.5, color=fc,
            fontweight='bold', zorder=6)

# Relation lines in legend
yy_rel = ly - 6.5
ax.plot([lx + 0.6, lx + 2.8], [yy_rel, yy_rel],
        color=COLOR_REL, lw=2.2, zorder=6)
ax.text(lx + 3.8, yy_rel, 'Relation 1  ->  *', va='center', fontsize=11.5, color='#37474F', zorder=6)

yy_vrel = ly - 7.8
ax.plot([lx + 0.6, lx + 2.8], [yy_vrel, yy_vrel],
        color=COLOR_REL_VIEW, lw=2.2, linestyle=(0, (6, 4)), zorder=6)
ax.text(lx + 3.8, yy_vrel, 'Agrégation par Vue SQL', va='center', fontsize=11.5, color='#37474F', zorder=6)

# ── Save ─────────────────────────────────────────────────────────────────────
out = r'd:\Projet_stage_eranov_academie\Backend_ECarto\backend\ecarto_uml_diagram.png'
plt.tight_layout(pad=1.0)
plt.savefig(out, dpi=250, bbox_inches='tight', facecolor=COLOR_BG)
print(f'Saved: {out}')
