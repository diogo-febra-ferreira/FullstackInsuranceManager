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
                                    :to="`${client.username}/registerOccurrence`">Register
                                    Occurrence</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`${client.username}/myOccurrences`">See my
                                    Occurrences</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`${client.username}/myPolicies`">See my
                                    Policies</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 active" aria-current="page"><nuxt-link
                                    :to="`${client.username}/registerPolicy`">Register
                                    Policy</nuxt-link></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link mx-2 btn btn-danger" @click.prevent="signOut">Sign Out</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="mx-auto" style="width: 600px; margin-top: 50px;">
            <b-container class="h-100">
                <b-row class="h-100">
                    <b-col md="8" class="d-flex flex-column align-items-center">
                        <h4>Client Details:</h4>
                        <p>Username: {{ client.username }}</p>
                        <p>Name: {{ client.name }}</p>
                        <p>Email: {{ client.email }}</p>
                    </b-col>
                </b-row>
            </b-container>
        </div>
    </div>
</template>
<script>
import axios from 'axios';

export default {
    data() {
        return {
            client: {},
            documents: [],
            documentsFields: ['filename', 'actions'],
        }
    },
    computed: {
        username() {
            return this.$route.params.username
        }
    },
    created() {
        this.$axios.$get(`api/clients/${this.username}`)
            .then(client => this.client = client || {}).catch(() => {
                console.log("Error retrieving client")
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

<style>

</style>
