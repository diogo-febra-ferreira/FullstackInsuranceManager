<template>
    <div>
        <nav class="navbar navbar-expand-md navbar-dark bg-dark">
            <div class="container">
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                    data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false"
                    aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarNavDropdown">
                    <ul class="navbar-nav mx-auto">
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`/clients/${this.$auth.user.username}`">Profile</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`registerOccurrence`">Register
                                    Occurrence</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link :to="`myOccurrences`">See my
                                    Occurrences</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link :to="`myPolicies`">See my
                                    Policies</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`registerPolicy`">Register
                                    Policy</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div>
            <div style="text-align: center; padding: 50px;">
                <h3>My Policies</h3>
            </div>
            <b-container>
                <b-table striped over :items="policies" :fields="fields">
                    <template v-slot:cell(actions)="row">
                        <nuxt-link class="btn btn-link" :to="`${row.item.code}`">Details
                        </nuxt-link>
                    </template>
                    <template v-slot:cell(client)="row">
                        {{ row.item.client.name }}
                    </template>
                </b-table>
                <nuxt-link to="/">Back</nuxt-link>
            </b-container>

        </div>
    </div>


</template>
<script>

export default {
    data() {
        return {
            fields: ['code', 'type', 'covers', 'insurance'],
            policies: []
        }
    },
    computed: {
        username() {
            return this.$route.params.username
        }
    },
    created() {
        this.$axios.$get(`api/policies/client/${this.username}`)
            .then((policies) => {
                this.policies = policies
            }).catch(() => {
                console.log("Error retrieving policies")
            })
    },
    methods: {
        signOut() {
            this.$auth.logout()
            this.$router.push('/')
        }
    }
}


</script>
