package com.example.casosemestral.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

enum class UserRole(val roleName: String) {
    GUEST("Invitado"),
    DONOR("Donante / Empresa Colaboradora"),
    ADMIN_TEAM("Equipo de Vinculación (Admin)")
}

enum class CollaborationType(val displayName: String, val description: String) {
    DONACION_TRATAMIENTO("Donación para Tratamientos", "Aporta directamente al financiamiento de medicamentos y cirugías."),
    DONACION_EQUIPAMIENTO("Aporte de Equipamiento Médico", "Financia o dona sillas de ruedas, insumos o ayudas técnicas."),
    VOLUNTARIADO_SALUD("Voluntariado Médico / Salud", "Súmate como profesional de la salud o apoyo logístico en operativos."),
    ALIANZA_EMPRESA_SALUD("Alianza Corporativa de Salud RSE", "Programas de salud comunitaria y RSE para tu empresa.")
}

data class ImpactMetric(
    val title: String,
    val value: String,
    val description: String,
    val icon: ImageVector
)

data class Program(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val targetAudience: String,
    val duration: String,
    val badge: String
)

data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val category: String,
    val description: String,
    val impactNote: String,
    val isPopular: Boolean = false
)

data class SuccessStory(
    val id: String,
    val title: String,
    val protagonist: String,
    val quote: String,
    val storyText: String,
    val impactBadge: String,
    val year: String
)

data class InterestRegistration(
    val id: Long = 0L,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val organization: String = "",
    val type: CollaborationType = CollaborationType.DONACION_TRATAMIENTO,
    val message: String = "",
    val isContacted: Boolean = false
)


object CorpalivRepository {

    val impactMetrics = listOf(
        ImpactMetric(
            title = "Atenciones Médicas",
            value = "1.200+",
            description = "Pacientes vulnerables atendidos con tratamientos financia dos.",
            icon = Icons.Default.MedicalServices
        ),
        ImpactMetric(
            title = "Equipos Entregados",
            value = "350+",
            description = "Sillas de ruedas, camas clínicas y ayudas técnicas otorgadas.",
            icon = Icons.Default.Accessible
        ),
        ImpactMetric(
            title = "Operativos de Salud",
            value = "45+",
            description = "Jornadas comunitarias de atención preventiva y especialidades.",
            icon = Icons.Default.LocalHospital
        ),
        ImpactMetric(
            title = "Transparencia Total",
            value = "100%",
            description = "De las donaciones destinadas a tratamientos e insumos sanitarios.",
            icon = Icons.Default.VolunteerActivism
        )
    )

    val programs = listOf(
        Program(
            id = "prog_med_1",
            title = "Fondo de Salud y Tratamientos Críticos",
            category = "Tratamientos",
            description = "Financiamiento de medicamentos de alto costo, insumos médicos y cirugías para personas en situación de vulnerabilidad.",
            targetAudience = "Pacientes con enfermedades complejas o crónicas sin cobertura suficiente.",
            duration = "Atención Inmediata",
            badge = "Prioridad Médica"
        ),
        Program(
            id = "prog_med_2",
            title = "Banco de Ayudas Técnicas y Movilidad",
            category = "Equipamiento",
            description = "Entrega y préstamo gratuito de sillas de ruedas adaptadas, burritos, camas clínicas y concentradores de oxígeno.",
            targetAudience = "Adultos mayores y personas con movilidad reducida.",
            duration = "Acompañamiento Continuo",
            badge = "Alta Demanda"
        ),
        Program(
            id = "prog_med_3",
            title = "Operativos Sanitarios Comunitarios",
            category = "Prevención",
            description = "Jornadas territoriales de medicina general, odontología, kinesiología y exámenes preventivos en zonas vulnerables.",
            targetAudience = "Comunidades alejadas o con acceso limitado a salud.",
            duration = "Jornadas Mensuales",
            badge = "Impacto Territorial"
        ),
        Program(
            id = "prog_med_4",
            title = "Programa RSE: Salud Corporativa & Apadrinamiento",
            category = "Alianzas RSE",
            description = "Alianzas estratégicas para que empresas apadrinen tratamientos médicos de pacientes o equipamiento sanitario.",
            targetAudience = "Empresas con programas de RSE e impacto en salud.",
            duration = "Anual / Renovable",
            badge = "Certificación Social"
        )
    )

    val products = listOf(
        Product(
            id = "prod_med_1",
            name = "Kit de Insumos Médicos & Curaciones",
            price = 10000,
            category = "Insumos Médicos",
            description = "Aporte para un paquete completo de curaciones, apósitos y material esterilizado para un paciente en tratamiento en casa.",
            impactNote = "Cubre los insumos semanales de un paciente electrodependiente.",
            isPopular = true
        ),
        Product(
            id = "prod_med_2",
            name = "Silla de Ruedas Ergonómica Adaptada",
            price = 85000,
            category = "Equipamiento",
            description = "Financia una silla de ruedas ultraligera y plegable para devolver la movilidad y autonomía a una persona necesitada.",
            impactNote = "Permite la reintegración social y traslados médicos seguros.",
            isPopular = true
        ),
        Product(
            id = "prod_med_3",
            name = "Examen Médico de Alta Complejidad",
            price = 35000,
            category = "Exámenes & Cirugía",
            description = "Cubre el costo de scanner, resonancia magnética o biopsia urgente para acelerar diagnósticos médicos críticos.",
            impactNote = "Reduce semanas de espera en diagnósticos médicos urgentes.",
            isPopular = false
        ),
        Product(
            id = "prod_med_4",
            name = "Caja de Suplementos Clínicos y Nutrición",
            price = 25000,
            category = "Insumos Médicos",
            description = "Suplementos nutricionales enterales de alta densidad calórica para pacientes en recuperación oncológica o posoperatoria.",
            impactNote = "Asegura la nutrición adecuada durante 1 mes de tratamiento.",
            isPopular = false
        )
    )

    val successStories = listOf(
        SuccessStory(
            id = "story_med_1",
            title = "La recuperación oportuna de Sofía",
            protagonist = "Sofía (6 años) y su familia",
            quote = "Gracias a la donación rápida del equipo médico y los fármacos, Sofía pudo iniciar su tratamiento sin demoras.",
            storyText = "Sofía requería un tratamiento especializado inmediato. A través del Fondo de Salud, la fundación financió sus medicamentos y facilitó una silla de ruedas adaptada.",
            impactBadge = "Tratamiento Exitoso",
            year = "2024"
        ),
        SuccessStory(
            id = "story_med_2",
            title = "Operativo de Salud en Comunidad Rural",
            protagonist = "Equipo Médico Voluntario + Fundación",
            quote = "Atendimos a más de 120 personas que llevaban meses esperando un diagnóstico médico de especialidad.",
            storyText = "Un equipo multidisciplinario de médicos, enfermeros y kinesiólogos voluntarios realizó exámenes, curaciones y entrega gratuita de lentes y fármacos.",
            impactBadge = "Operativo Comunitario",
            year = "2023"
        )
    )
}
